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
import net.wanho.fitai.modules.system.mapper.SysNoticeMapper;
import net.wanho.fitai.modules.system.model.dto.SysNoticeAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysNoticeUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysNotice;
import net.wanho.fitai.modules.system.model.po.SysNoticeRead;
import net.wanho.fitai.modules.system.model.vo.SysNoticeVO;
import net.wanho.fitai.modules.system.service.SysNoticeReadServiceI;
import net.wanho.fitai.modules.system.service.SysNoticeServiceI;
import net.wanho.fitai.modules.system.sse.NoticeSseHub;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends MPJBaseServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeServiceI {

    private final Converter converter;
    private final SysNoticeReadServiceI sysNoticeReadService;
    private final NoticeSseHub noticeSseHub;


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

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysNoticeVO findById(Long id) {
        SysNotice entity = this.getById(id);
        return converter.convert(entity, SysNoticeVO.class);
    }

    @Override
    public void publish(Long noticeId) {

        SysNotice dbNotice = this.getById(noticeId);

        SysNotice sysNotice = new SysNotice();
        sysNotice.setNoticeId(noticeId);
        sysNotice.setStatus("1");
        sysNotice.setPublishTime(LocalDateTime.now());
        this.updateById(sysNotice);

        // sse向所有用户推送
        Map<String, Object> map = new HashMap<>();
        map.put("noticeId", dbNotice.getNoticeId());
        map.put("noticeTitle", dbNotice.getNoticeTitle());
        map.put("noticeContent", dbNotice.getNoticeContent());
        map.put("noticeType", dbNotice.getNoticeType());
        map.put("publishTime", sysNotice.getPublishTime());
        noticeSseHub.broadcast(map);

    }

    @Override
    public void revoke(Long noticeId) {
        SysNotice sysNotice = new SysNotice();
        sysNotice.setNoticeId(noticeId);
        sysNotice.setStatus("2");
        sysNotice.setPublishTime(LocalDateTime.now());
        this.updateById(sysNotice);
    }

    @Override
    public Long readStats(Long noticeId) {
        return sysNoticeReadService.count(new LambdaQueryWrapper<SysNoticeRead>().eq(SysNoticeRead::getNoticeId, noticeId));
    }

    @Override
    public List<SysNoticeRead> readList(Long noticeId) {
        return sysNoticeReadService.list(
                new LambdaQueryWrapper<SysNoticeRead>()
                        .eq(SysNoticeRead::getNoticeId, noticeId)
                        .orderByDesc(SysNoticeRead::getReadTime)
        );
    }

    @Override
    public List<Long> myReadIds() {
        Wrapper<SysNoticeRead> wrapper = new LambdaQueryWrapper<SysNoticeRead>().eq(SysNoticeRead::getUserId, AdminLoginHelper.getUserId());
        List<SysNoticeRead> allReads = sysNoticeReadService.list(wrapper);
        return allReads.stream().map(SysNoticeRead::getNoticeId).filter(noticeId -> ObjectUtil.isNotNull(noticeId)).distinct().toList();
    }

    @Override
    public void readBatch(List<Long> noticeIds) {

        List<Long> existsId = myReadIds();

        ArrayList<SysNoticeRead> sysNoticeReadList = new ArrayList<>();
       for(Long noticeId:existsId){
           if(existsId.contains(noticeId)) continue;;
           SysNoticeRead sysNoticeRead = new SysNoticeRead();
           sysNoticeRead.setNoticeId(noticeId);
           sysNoticeRead.setReadId(AdminLoginHelper.getDeptId());
           sysNoticeRead.setReadTime(LocalDateTime.now());
           sysNoticeReadList.add(sysNoticeRead);
       }

        sysNoticeReadService.saveBatch(sysNoticeReadList);

    }

    @Override
    public void markRead(Long noticeId) {
        SysNoticeRead sysNoticeRead = new SysNoticeRead();
        sysNoticeRead.setNoticeId(noticeId);
        sysNoticeRead.setReadId(AdminLoginHelper.getDeptId());
        sysNoticeRead.setReadTime(LocalDateTime.now());
        sysNoticeReadService.save(sysNoticeRead);
    }
}
