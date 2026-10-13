package net.wanho.fitai.modules.community.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.community.mapper.CmsCommentLikeMapper;
import net.wanho.fitai.modules.community.model.dto.CmsCommentLikeAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentLikeQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsCommentLikeUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsCommentLike;
import net.wanho.fitai.modules.community.model.vo.CmsCommentLikeVO;
import net.wanho.fitai.modules.community.service.CmsCommentLikeServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CmsCommentLikeServiceImpl extends MPJBaseServiceImpl<CmsCommentLikeMapper, CmsCommentLike> implements CmsCommentLikeServiceI {

    private final Converter converter;

    @Override
    public IPage<CmsCommentLikeVO> findByPage(CmsCommentLikeQueryDTO queryDto) {

        IPage<CmsCommentLike> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CmsCommentLike> wrapper = new LambdaQueryWrapper<>();
        IPage<CmsCommentLike> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CmsCommentLikeVO.class));

    }

    @Override
    public void add(CmsCommentLikeAddDTO addDto) {
        CmsCommentLike cmscommentlike = converter.convert(addDto, CmsCommentLike.class);
        this.save(cmscommentlike);
    }

    @Override
    public void update(CmsCommentLikeUpdateDTO updateDto) {
        CmsCommentLike cmscommentlike = converter.convert(updateDto, CmsCommentLike.class);
        this.updateById(cmscommentlike);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CmsCommentLikeVO findById(Long id) {
        CmsCommentLike entity = this.getById(id);
        return converter.convert(entity, CmsCommentLikeVO.class);
    }
}
