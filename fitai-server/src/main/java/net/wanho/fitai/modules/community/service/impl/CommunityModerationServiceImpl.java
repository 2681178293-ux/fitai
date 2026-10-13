package net.wanho.fitai.modules.community.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.community.mapper.CommunityModerationMapper;
import net.wanho.fitai.modules.community.model.dto.*;
import net.wanho.fitai.modules.community.model.po.CommunityModeration;
import net.wanho.fitai.modules.community.model.vo.CommunityModerationVO;
import net.wanho.fitai.modules.community.service.CommunityModerationServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CommunityModerationServiceImpl extends MPJBaseServiceImpl<CommunityModerationMapper, CommunityModeration> implements CommunityModerationServiceI {

    private final Converter converter;

    @Override
    public IPage<CommunityModerationVO> findByPage(CommunityModerationQueryDTO queryDto) {

        IPage<CommunityModeration> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CommunityModeration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityModeration::getAuditStatus, queryDto.getAuditStatus());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getTargetType()), CommunityModeration::getTargetType, queryDto.getTargetType());
        IPage<CommunityModeration> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CommunityModerationVO.class));

    }

    @Override
    public void reject(CommunityModerationRejectDTO rejectDto) {

        CommunityModeration communityModeration = new CommunityModeration();
        communityModeration.setId(rejectDto.getModerationId());
        communityModeration.setAuditStatus(2);
        communityModeration.setAuditReason(rejectDto.getReason());
        this.updateById(communityModeration);

    }

    @Override
    public IPage<CommunityModerationVO> historyPage(CommunityModerationHistoryQueryDTO queryDto) {
        IPage<CommunityModeration> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CommunityModeration> wrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotNull(queryDto.getAuditStatus())){
            wrapper.eq(CommunityModeration::getAuditStatus, queryDto.getAuditStatus());
        }else{
            wrapper.ne(CommunityModeration::getAuditStatus, 0);
        }
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getTargetType()), CommunityModeration::getTargetType, queryDto.getTargetType());
        IPage<CommunityModeration> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CommunityModerationVO.class));
    }






    @Override
    public void add(CommunityModerationAddDTO addDto) {
        CommunityModeration communitymoderation = converter.convert(addDto, CommunityModeration.class);
        this.save(communitymoderation);
    }

    @Override
    public void update(CommunityModerationUpdateDTO updateDto) {
        CommunityModeration communitymoderation = converter.convert(updateDto, CommunityModeration.class);
        this.updateById(communitymoderation);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CommunityModerationVO findById(Long id) {
        CommunityModeration entity = this.getById(id);
        return converter.convert(entity, CommunityModerationVO.class);
    }


}
