package net.wanho.fitai.modules.diet.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.diet.mapper.DietRecordMapper;
import net.wanho.fitai.modules.diet.model.dto.DietRecordAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietRecordQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietRecordUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietRecord;
import net.wanho.fitai.modules.diet.model.vo.DietRecordVO;
import net.wanho.fitai.modules.diet.service.DietRecordServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DietRecordServiceImpl extends MPJBaseServiceImpl<DietRecordMapper, DietRecord> implements DietRecordServiceI {

    private final Converter converter;

    @Override
    public IPage<DietRecordVO> findByPage(DietRecordQueryDTO queryDto) {

        IPage<DietRecord> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<DietRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getFoodName()), DietRecord::getFoodName, queryDto.getFoodName());
        IPage<DietRecord> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, DietRecordVO.class));

    }

    @Override
    public void add(DietRecordAddDTO addDto) {
        DietRecord dietrecord = converter.convert(addDto, DietRecord.class);
        this.save(dietrecord);
    }

    @Override
    public void update(DietRecordUpdateDTO updateDto) {
        DietRecord dietrecord = converter.convert(updateDto, DietRecord.class);
        this.updateById(dietrecord);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public DietRecordVO findById(Long id) {
        DietRecord entity = this.getById(id);
        return converter.convert(entity, DietRecordVO.class);
    }
}
