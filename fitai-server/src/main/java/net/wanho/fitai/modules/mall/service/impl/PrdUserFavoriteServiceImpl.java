package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdUserFavoriteMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdUserFavoriteAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdUserFavoriteQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdUserFavoriteUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdUserFavorite;
import net.wanho.fitai.modules.mall.model.vo.PrdUserFavoriteVO;
import net.wanho.fitai.modules.mall.service.PrdUserFavoriteServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdUserFavoriteServiceImpl extends MPJBaseServiceImpl<PrdUserFavoriteMapper, PrdUserFavorite> implements PrdUserFavoriteServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdUserFavoriteVO> findByPage(PrdUserFavoriteQueryDTO queryDto) {

        IPage<PrdUserFavorite> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdUserFavorite> wrapper = new LambdaQueryWrapper<>();
        IPage<PrdUserFavorite> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdUserFavoriteVO.class));

    }

    @Override
    public void add(PrdUserFavoriteAddDTO addDto) {
        PrdUserFavorite prduserfavorite = converter.convert(addDto, PrdUserFavorite.class);
        this.save(prduserfavorite);
    }

    @Override
    public void update(PrdUserFavoriteUpdateDTO updateDto) {
        PrdUserFavorite prduserfavorite = converter.convert(updateDto, PrdUserFavorite.class);
        this.updateById(prduserfavorite);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdUserFavoriteVO findById(Long id) {
        PrdUserFavorite entity = this.getById(id);
        return converter.convert(entity, PrdUserFavoriteVO.class);
    }
}
