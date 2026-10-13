package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdSpuImageMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuImageAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuImageQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuImageUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdSpuImage;
import net.wanho.fitai.modules.mall.model.vo.PrdSpuImageVO;
import net.wanho.fitai.modules.mall.service.PrdSpuImageServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdSpuImageServiceImpl extends MPJBaseServiceImpl<PrdSpuImageMapper, PrdSpuImage> implements PrdSpuImageServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdSpuImageVO> findByPage(PrdSpuImageQueryDTO queryDto) {

        IPage<PrdSpuImage> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdSpuImage> wrapper = new LambdaQueryWrapper<>();
        IPage<PrdSpuImage> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdSpuImageVO.class));

    }

    @Override
    public void add(PrdSpuImageAddDTO addDto) {
        PrdSpuImage prdspuimage = converter.convert(addDto, PrdSpuImage.class);
        this.save(prdspuimage);
    }

    @Override
    public void update(PrdSpuImageUpdateDTO updateDto) {
        PrdSpuImage prdspuimage = converter.convert(updateDto, PrdSpuImage.class);
        this.updateById(prdspuimage);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdSpuImageVO findById(Long id) {
        PrdSpuImage entity = this.getById(id);
        return converter.convert(entity, PrdSpuImageVO.class);
    }
}
