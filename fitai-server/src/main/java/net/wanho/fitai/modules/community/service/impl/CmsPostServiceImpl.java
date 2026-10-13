package net.wanho.fitai.modules.community.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.community.mapper.CmsPostMapper;
import net.wanho.fitai.modules.community.model.dto.CmsPostAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsPost;
import net.wanho.fitai.modules.community.model.vo.CmsPostVO;
import net.wanho.fitai.modules.community.service.CmsPostServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CmsPostServiceImpl extends MPJBaseServiceImpl<CmsPostMapper, CmsPost> implements CmsPostServiceI {

    private final Converter converter;

    @Override
    public IPage<CmsPostVO> findByPage(CmsPostQueryDTO queryDto) {

        IPage<CmsPost> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CmsPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCustomerName()), CmsPost::getCustomerName, queryDto.getCustomerName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), CmsPost::getStatus, queryDto.getStatus());
        IPage<CmsPost> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CmsPostVO.class));

    }

    @Override
    public void add(CmsPostAddDTO addDto) {
        CmsPost cmspost = converter.convert(addDto, CmsPost.class);
        this.save(cmspost);
    }

    @Override
    public void update(CmsPostUpdateDTO updateDto) {
        CmsPost cmspost = converter.convert(updateDto, CmsPost.class);
        this.updateById(cmspost);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CmsPostVO findById(Long id) {
        CmsPost entity = this.getById(id);
        return converter.convert(entity, CmsPostVO.class);
    }
}
