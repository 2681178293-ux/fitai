package net.wanho.fitai.modules.course.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.course.mapper.CrsCourseMapper;
import net.wanho.fitai.modules.course.model.dto.CrsCourseAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsCourse;
import net.wanho.fitai.modules.course.model.vo.CrsCourseVO;
import net.wanho.fitai.modules.course.service.CrsCourseServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CrsCourseServiceImpl extends MPJBaseServiceImpl<CrsCourseMapper, CrsCourse> implements CrsCourseServiceI {

    private final Converter converter;

    @Override
    public IPage<CrsCourseVO> findByPage(CrsCourseQueryDTO queryDto) {

        IPage<CrsCourse> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CrsCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getKeyword()), CrsCourse::getCategoryName, queryDto.getKeyword())
                .or()
                .like(StrUtil.isNotEmpty(queryDto.getKeyword()), CrsCourse::getCourseTitle, queryDto.getKeyword());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), CrsCourse::getStatus, queryDto.getStatus());
        IPage<CrsCourse> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CrsCourseVO.class));
    }
    
    @Override
    public void add(CrsCourseAddDTO addDto) {
        CrsCourse crscourse = converter.convert(addDto, CrsCourse.class);
        this.save(crscourse);
    }

    @Override
    public void update(CrsCourseUpdateDTO updateDto) {
        CrsCourse crscourse = converter.convert(updateDto, CrsCourse.class);
        this.updateById(crscourse);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }







    @Override
    public CrsCourseVO findById(Long id) {
        CrsCourse entity = this.getById(id);
        return converter.convert(entity, CrsCourseVO.class);
    }
}
