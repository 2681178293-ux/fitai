package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdSpuDetailMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuDetailAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuDetailQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdSpuDetailUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdSpuDetail;
import net.wanho.fitai.modules.mall.model.vo.PrdSpuDetailVO;
import net.wanho.fitai.modules.mall.service.PrdSpuDetailServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdSpuDetailServiceImpl extends MPJBaseServiceImpl<PrdSpuDetailMapper, PrdSpuDetail> implements PrdSpuDetailServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdSpuDetailVO> findByPage(PrdSpuDetailQueryDTO queryDto) {

        IPage<PrdSpuDetail> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdSpuDetail> wrapper = new LambdaQueryWrapper<>();
        IPage<PrdSpuDetail> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdSpuDetailVO.class));

    }

    @Override
    public void add(PrdSpuDetailAddDTO addDto) {
        PrdSpuDetail prdspudetail = converter.convert(addDto, PrdSpuDetail.class);
        this.save(prdspudetail);
    }

    @Override
    public void update(PrdSpuDetailUpdateDTO updateDto) {
        PrdSpuDetail prdspudetail = converter.convert(updateDto, PrdSpuDetail.class);
        this.updateById(prdspudetail);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdSpuDetailVO findById(Long id) {
        PrdSpuDetail entity = this.getById(id);
        return converter.convert(entity, PrdSpuDetailVO.class);
    }
}
