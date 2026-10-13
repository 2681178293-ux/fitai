package net.wanho.fitai.modules.mall.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.mall.mapper.PrdAttrValueMapper;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrValueAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrValueQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdAttrValueUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.PrdAttrValue;
import net.wanho.fitai.modules.mall.model.vo.PrdAttrValueVO;
import net.wanho.fitai.modules.mall.service.PrdAttrValueServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PrdAttrValueServiceImpl extends MPJBaseServiceImpl<PrdAttrValueMapper, PrdAttrValue> implements PrdAttrValueServiceI {

    private final Converter converter;

    @Override
    public IPage<PrdAttrValueVO> findByPage(PrdAttrValueQueryDTO queryDto) {

        IPage<PrdAttrValue> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<PrdAttrValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), PrdAttrValue::getStatus, queryDto.getStatus());
        IPage<PrdAttrValue> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, PrdAttrValueVO.class));

    }

    @Override
    public void add(PrdAttrValueAddDTO addDto) {
        PrdAttrValue prdattrvalue = converter.convert(addDto, PrdAttrValue.class);
        this.save(prdattrvalue);
    }

    @Override
    public void update(PrdAttrValueUpdateDTO updateDto) {
        PrdAttrValue prdattrvalue = converter.convert(updateDto, PrdAttrValue.class);
        this.updateById(prdattrvalue);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public PrdAttrValueVO findById(Long id) {
        PrdAttrValue entity = this.getById(id);
        return converter.convert(entity, PrdAttrValueVO.class);
    }
}
