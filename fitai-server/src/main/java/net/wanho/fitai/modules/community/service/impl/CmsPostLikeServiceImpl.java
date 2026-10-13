package net.wanho.fitai.modules.community.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.community.mapper.CmsPostLikeMapper;
import net.wanho.fitai.modules.community.model.dto.CmsPostLikeAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostLikeQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsPostLikeUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsPostLike;
import net.wanho.fitai.modules.community.model.vo.CmsPostLikeVO;
import net.wanho.fitai.modules.community.service.CmsPostLikeServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CmsPostLikeServiceImpl extends MPJBaseServiceImpl<CmsPostLikeMapper, CmsPostLike> implements CmsPostLikeServiceI {

    private final Converter converter;

    @Override
    public IPage<CmsPostLikeVO> findByPage(CmsPostLikeQueryDTO queryDto) {

        IPage<CmsPostLike> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CmsPostLike> wrapper = new LambdaQueryWrapper<>();
        IPage<CmsPostLike> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CmsPostLikeVO.class));

    }

    @Override
    public void add(CmsPostLikeAddDTO addDto) {
        CmsPostLike cmspostlike = converter.convert(addDto, CmsPostLike.class);
        this.save(cmspostlike);
    }

    @Override
    public void update(CmsPostLikeUpdateDTO updateDto) {
        CmsPostLike cmspostlike = converter.convert(updateDto, CmsPostLike.class);
        this.updateById(cmspostlike);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CmsPostLikeVO findById(Long id) {
        CmsPostLike entity = this.getById(id);
        return converter.convert(entity, CmsPostLikeVO.class);
    }
}
