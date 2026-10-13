package net.wanho.fitai.modules.course.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.exception.CustomException;
import net.wanho.fitai.modules.course.code.CourseCode;
import net.wanho.fitai.modules.course.mapper.CrsCourseMapper;
import net.wanho.fitai.modules.course.mapper.CrsLearnProgressMapper;
import net.wanho.fitai.modules.course.model.dto.CrsLearnProgressAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsLearnProgressQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsLearnProgressUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsCourse;
import net.wanho.fitai.modules.course.model.po.CrsLearnProgress;
import net.wanho.fitai.modules.course.model.vo.CrsLearnProgressStatVO;
import net.wanho.fitai.modules.course.model.vo.CrsLearnProgressVO;
import net.wanho.fitai.modules.course.service.CrsLearnProgressServiceI;
import net.wanho.fitai.modules.member.mapper.UsrCustomerMapper;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrsLearnProgressServiceImpl extends MPJBaseServiceImpl<CrsLearnProgressMapper, CrsLearnProgress> implements CrsLearnProgressServiceI {

    private final Converter converter;
    private final UsrCustomerMapper usrCustomerMapper;
    private final CrsCourseMapper crsCourseMapper;


    /**
     * 某课程下的学员学习列表：查进度 → 批量取用户昵称 → 组装 VO
     */
    @Override
    public List<CrsLearnProgressVO> findStudentsByCourseId(Long courseId) {
        // 1. 按课程查进度；已完成优先，同状态按最近更新时间倒序
        List<CrsLearnProgress> crsLearnProgressList = this.list(new LambdaQueryWrapper<CrsLearnProgress>()
                .eq(CrsLearnProgress::getCourseId, courseId)
                .orderByDesc(CrsLearnProgress::getIsFinished)
                .orderByDesc(CrsLearnProgress::getUpdateTime)
        );

        if (CollUtil.isEmpty(crsLearnProgressList)) {
            return null;
        }

        // 2. 收集学员 ID，一次查出昵称，避免 N+1
        // 这里用的不是表连接，而是通过获取另一张表的数据，再进行组装
        List<Long> customerIds = crsLearnProgressList.stream()
                .map(CrsLearnProgress::getCustomerId)
                .distinct()
                .toList();
        Map<Long, String> customerMap = usrCustomerMapper.selectByIds(customerIds).stream()
                .collect(Collectors.toMap(UsrCustomer::getCustomerId, UsrCustomer::getNickName));

        // 3. 实体转 VO，并回填昵称
        return crsLearnProgressList.stream().map(item -> {
            CrsLearnProgressVO learnProgressVO = converter.convert(item, CrsLearnProgressVO.class);
            learnProgressVO.setNickName(customerMap.get(item.getCustomerId()));
            return learnProgressVO;
        }).toList();
    }

    /**
     * 课程完成率统计：校验课程存在 → 统计学习人数/完课人数 → 算完成率（%）
     */
    @Override
    public CrsLearnProgressStatVO stats(Long courseId) {
        CrsCourse crsCourse = crsCourseMapper.selectById(courseId);
        if (ObjectUtil.isEmpty(crsCourse)) {
            throw new CustomException(CourseCode.COURSE_NOT_FOUND);
        }

        // 该课程学习总人数；无人学习直接返回 null
        Long totalLearners = this.baseMapper.selectCount(
                new LambdaQueryWrapper<CrsLearnProgress>().eq(CrsLearnProgress::getCourseId, courseId));
        if (totalLearners.equals(0L)) {
            return null;
        }

        // 完课人数（isFinished = 1）
        Long completedCount = this.baseMapper.selectCount(new LambdaQueryWrapper<CrsLearnProgress>()
                .eq(CrsLearnProgress::getCourseId, courseId)
                .eq(CrsLearnProgress::getIsFinished, 1));

        // 完成率 = 完课人数 / 学习人数 * 100，保留 2 位小数
        CrsLearnProgressStatVO statVO = new CrsLearnProgressStatVO();
        statVO.setCourseId(courseId);
        statVO.setCourseTitle(crsCourse.getCourseTitle());
        statVO.setTotalLearners(totalLearners);
        statVO.setCompletedCount(completedCount);
        statVO.setCompletionRate(BigDecimal.valueOf(completedCount * 100.0)
                .divide(BigDecimal.valueOf(totalLearners), 2, BigDecimal.ROUND_HALF_UP));
        return statVO;
    }

    /**
     * 学习进度分页：左联 usr_customer，带出昵称/手机/头像/会员等级供管理端列表展示
     */
    @Override
    public IPage<CrsLearnProgressVO> findByPage(CrsLearnProgressQueryDTO queryDto) {
        IPage<CrsLearnProgressVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        MPJLambdaWrapper<CrsLearnProgress> wrapper = new MPJLambdaWrapper<>();
        // 学员展示字段（来自会员表）
        wrapper.selectAs(UsrCustomer::getNickName, CrsLearnProgressVO::getNickName);
        wrapper.selectAs(UsrCustomer::getMobile, CrsLearnProgressVO::getMobile);
        wrapper.selectAs(UsrCustomer::getAvatar, CrsLearnProgressVO::getAvatar);
        wrapper.selectAs(UsrCustomer::getMemberLevelName, CrsLearnProgressVO::getMemberLevelName);
        // 进度业务字段
        wrapper.selectAs(CrsLearnProgress::getProgressPct, CrsLearnProgressVO::getProgressPct);
        wrapper.selectAs(CrsLearnProgress::getCourseTitle, CrsLearnProgressVO::getCourseTitle);
        wrapper.selectAs(CrsLearnProgress::getIsFinished, CrsLearnProgressVO::getIsFinished);
        wrapper.selectAs(CrsLearnProgress::getLastLearnTime, CrsLearnProgressVO::getLastLearnTime);
        wrapper.selectAs(CrsLearnProgress::getCourseId, CrsLearnProgressVO::getCourseId);

        wrapper.leftJoin(UsrCustomer.class, UsrCustomer::getCustomerId, CrsLearnProgress::getCustomerId);
        // 可选条件：课程名、课时名模糊查
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCourseTitle()), CrsLearnProgress::getCourseTitle, queryDto.getCourseTitle());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getVideoTitle()), CrsLearnProgress::getVideoTitle, queryDto.getVideoTitle());

        return this.baseMapper.selectJoinPage(page, CrsLearnProgressVO.class, wrapper);
    }

    @Override
    public void add(CrsLearnProgressAddDTO addDto) {
        CrsLearnProgress crslearnprogress = converter.convert(addDto, CrsLearnProgress.class);
        this.save(crslearnprogress);
    }

    @Override
    public void update(CrsLearnProgressUpdateDTO updateDto) {
        CrsLearnProgress crslearnprogress = converter.convert(updateDto, CrsLearnProgress.class);
        this.updateById(crslearnprogress);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CrsLearnProgressVO findById(Long id) {
        CrsLearnProgress entity = this.getById(id);
        return converter.convert(entity, CrsLearnProgressVO.class);
    }


}
