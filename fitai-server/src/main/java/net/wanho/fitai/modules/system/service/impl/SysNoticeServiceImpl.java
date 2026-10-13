package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.auth.helper.AdminLoginHelper;
import net.wanho.fitai.modules.member.mapper.UsrCustomerMapper;
import net.wanho.fitai.modules.system.mapper.SysNoticeMapper;
import net.wanho.fitai.modules.system.mapper.SysUserMapper;
import net.wanho.fitai.modules.system.model.dto.SysNoticeAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysNotice;
import net.wanho.fitai.modules.system.model.po.SysNoticeRead;
import net.wanho.fitai.modules.system.model.po.SysUser;
import net.wanho.fitai.modules.system.model.vo.SysNoticeVO;
import net.wanho.fitai.modules.system.service.SysNoticeReadServiceI;
import net.wanho.fitai.modules.system.service.SysNoticeServiceI;
import net.wanho.fitai.modules.system.sse.NoticeSseConnectionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends MPJBaseServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeServiceI {

    private final Converter converter;
    private final SysNoticeReadServiceI sysNoticeReadService;
    private final NoticeSseConnectionManager connectionManager;
    private final UsrCustomerMapper usrCustomerMapper;
    private final SysUserMapper sysUserMapper;


    /** 分页查询公告列表 */
    @Override
    public IPage<SysNoticeVO> findByPage(SysNoticeQueryDTO queryDto) {
        IPage<SysNotice> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getNoticeTitle()), SysNotice::getNoticeTitle, queryDto.getNoticeTitle());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getNoticeType()), SysNotice::getNoticeType, queryDto.getNoticeType());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysNotice::getStatus, queryDto.getStatus());
        IPage<SysNotice> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysNoticeVO.class));
    }

    @Override
    public void add(SysNoticeAddDTO addDto) {
        SysNotice entity = converter.convert(addDto, SysNotice.class);
        this.save(entity);
    }

    @Override
    public void update(SysNoticeUpdateDTO updateDto) {
        SysNotice entity = converter.convert(updateDto, SysNotice.class);
        this.updateById(entity);
    }

    /** 删除公告，并清理对应已读记录 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());

        sysNoticeReadService.remove(new LambdaQueryWrapper<SysNoticeRead>().in(SysNoticeRead::getNoticeId, id.split(",")));
    }

    @Override
    public SysNoticeVO findById(Long id) {
        SysNotice entity = this.getById(id);
        return converter.convert(entity, SysNoticeVO.class);
    }

    /** 发布公告，并通过 SSE 向在线用户推送 */
    @Override
    public void publish(Long noticeId) {

        SysNotice dbNotice = this.getById(noticeId);

        SysNotice sysNotice = new SysNotice();
        sysNotice.setNoticeId(noticeId);
        sysNotice.setStatus("1");
        sysNotice.setPublishTime(LocalDateTime.now());
        this.updateById(sysNotice);

        // sse向所有用户推送
        //把前端需要的数据封装成map
        Map<String, Object> map = new HashMap<>();
        map.put("noticeId", dbNotice.getNoticeId());
        map.put("noticeTitle", dbNotice.getNoticeTitle());
        map.put("noticeContent", dbNotice.getNoticeContent());
        map.put("noticeType", dbNotice.getNoticeType());
        map.put("publishTime", sysNotice.getPublishTime());

        // 推送
        //获取所有需要推送信息用户ID
        List<Long> userIds = sysUserMapper.selectList(
                        new LambdaQueryWrapper<SysUser>()
                                .eq(SysUser::getStatus, "0")
                )
                .stream()
                .map(SysUser::getUserId)
                .toList();

        connectionManager.send(userIds,map);

    }

    /** 撤回已发布的公告 */
    @Override
    public void revoke(Long noticeId) {
        SysNotice sysNotice = new SysNotice();
        sysNotice.setNoticeId(noticeId);
        sysNotice.setStatus("2");
        sysNotice.setPublishTime(LocalDateTime.now());
        this.updateById(sysNotice);
    }

    /** 统计某条公告的已读人数 */
    @Override
    public Long readStats(Long noticeId) {
        return sysNoticeReadService.count(new LambdaQueryWrapper<SysNoticeRead>().eq(SysNoticeRead::getNoticeId, noticeId));
    }

    /** 查询某条公告的已读明细列表 */
    @Override
    public List<SysNoticeRead> readList(Long noticeId) {
        return sysNoticeReadService.list(
                new LambdaQueryWrapper<SysNoticeRead>()
                        .eq(SysNoticeRead::getNoticeId, noticeId)
                        .orderByDesc(SysNoticeRead::getReadTime)
        );
    }

    /** 查询当前登录用户已读过的公告 ID 列表 */
    @Override
    public List<Long> myReadIds() {
        //查询当前登录用户已读过的公告 ID 列表
        Wrapper<SysNoticeRead> wrapper = new LambdaQueryWrapper<SysNoticeRead>()
            .eq(SysNoticeRead::getUserId, AdminLoginHelper.getUserId());
        List<SysNoticeRead> allReads = sysNoticeReadService.list(wrapper);
        //将已读过的公告 ID 返回
        return allReads.stream()
            .map(SysNoticeRead::getNoticeId)
            .filter(noticeId -> ObjectUtil.isNotNull(noticeId))
            .distinct().toList();
    }

    /** 批量已读 */
    @Override
    public void readBatch(List<Long> noticeIds) {

        List<Long> existsId = myReadIds();

        ArrayList<SysNoticeRead> sysNoticeReadList = new ArrayList<>();
        for(Long item:noticeIds){
            if(existsId.contains(item)) continue;
            SysNoticeRead sysNoticeRead = new SysNoticeRead();
            sysNoticeRead.setNoticeId(item);
            sysNoticeRead.setUserId(AdminLoginHelper.getUserId());
            sysNoticeRead.setReadTime(LocalDateTime.now());
            sysNoticeReadList.add(sysNoticeRead);
        }

        sysNoticeReadService.saveBatch(sysNoticeReadList);
    }

    /** 单条已读 */
    @Override
    public void markRead(Long noticeId) {

        List<Long> existsId = myReadIds();
        if(existsId.contains(noticeId)) return;

        SysNoticeRead sysNoticeRead = new SysNoticeRead();
        sysNoticeRead.setNoticeId(noticeId);
        sysNoticeRead.setUserId(AdminLoginHelper.getUserId());
        sysNoticeRead.setReadTime(LocalDateTime.now());
        sysNoticeReadService.save(sysNoticeRead);
    }
}
