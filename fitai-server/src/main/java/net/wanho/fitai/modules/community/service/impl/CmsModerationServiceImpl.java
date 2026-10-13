package net.wanho.fitai.modules.community.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.community.mapper.CmsModerationMapper;
import net.wanho.fitai.modules.community.model.dto.CmsModerationAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsModerationQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsModerationUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsModeration;
import net.wanho.fitai.modules.community.model.vo.CmsModerationVO;
import net.wanho.fitai.modules.community.service.CmsModerationServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CmsModerationServiceImpl extends MPJBaseServiceImpl<CmsModerationMapper, CmsModeration> implements CmsModerationServiceI {

    private final Converter converter;

    @Override
    public IPage<CmsModerationVO> findByPage(CmsModerationQueryDTO queryDto) {

        IPage<CmsModeration> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CmsModeration> wrapper = new LambdaQueryWrapper<>();
        IPage<CmsModeration> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CmsModerationVO.class));

    }

    @Override
    public void add(CmsModerationAddDTO addDto) {
        CmsModeration cmsmoderation = converter.convert(addDto, CmsModeration.class);
        this.save(cmsmoderation);
    }

    @Override
    public void update(CmsModerationUpdateDTO updateDto) {
        CmsModeration cmsmoderation = converter.convert(updateDto, CmsModeration.class);
        this.updateById(cmsmoderation);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CmsModerationVO findById(Long id) {
        CmsModeration entity = this.getById(id);
        return converter.convert(entity, CmsModerationVO.class);
    }
}
