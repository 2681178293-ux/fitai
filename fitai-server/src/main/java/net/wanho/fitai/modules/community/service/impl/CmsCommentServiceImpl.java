package net.wanho.fitai.modules.community.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.community.mapper.CmsCommentMapper;
import net.wanho.fitai.modules.community.model.dto.CmsCommentAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsComment;
import net.wanho.fitai.modules.community.model.vo.CmsCommentVO;
import net.wanho.fitai.modules.community.service.CmsCommentServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CmsCommentServiceImpl extends MPJBaseServiceImpl<CmsCommentMapper, CmsComment> implements CmsCommentServiceI {

    private final Converter converter;

    @Override
    public IPage<CmsCommentVO> findByPage(CmsCommentQueryDTO queryDto) {

        IPage<CmsComment> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CmsComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCustomerName()), CmsComment::getCustomerName, queryDto.getCustomerName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), CmsComment::getStatus, queryDto.getStatus());
        IPage<CmsComment> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CmsCommentVO.class));

    }

    @Override
    public void add(CmsCommentAddDTO addDto) {
        CmsComment cmscomment = converter.convert(addDto, CmsComment.class);
        this.save(cmscomment);
    }

    @Override
    public void update(CmsCommentUpdateDTO updateDto) {
        CmsComment cmscomment = converter.convert(updateDto, CmsComment.class);
        this.updateById(cmscomment);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CmsCommentVO findById(Long id) {
        CmsComment entity = this.getById(id);
        return converter.convert(entity, CmsCommentVO.class);
    }
}
