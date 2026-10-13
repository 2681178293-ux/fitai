package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiQuotaGiftMapper;
import net.wanho.fitai.modules.ai.model.dto.AiQuotaGiftAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiQuotaGiftQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiQuotaGiftUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiQuotaGift;
import net.wanho.fitai.modules.ai.model.vo.AiQuotaGiftVO;
import net.wanho.fitai.modules.ai.service.AiQuotaGiftServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiQuotaGiftServiceImpl extends MPJBaseServiceImpl<AiQuotaGiftMapper, AiQuotaGift> implements AiQuotaGiftServiceI {

    private final Converter converter;

    @Override
    public IPage<AiQuotaGiftVO> findByPage(AiQuotaGiftQueryDTO queryDto) {

        IPage<AiQuotaGift> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiQuotaGift> wrapper = new LambdaQueryWrapper<>();
        IPage<AiQuotaGift> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiQuotaGiftVO.class));

    }

    @Override
    public void add(AiQuotaGiftAddDTO addDto) {
        AiQuotaGift aiquotagift = converter.convert(addDto, AiQuotaGift.class);
        this.save(aiquotagift);
    }

    @Override
    public void update(AiQuotaGiftUpdateDTO updateDto) {
        AiQuotaGift aiquotagift = converter.convert(updateDto, AiQuotaGift.class);
        this.updateById(aiquotagift);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiQuotaGiftVO findById(Long id) {
        AiQuotaGift entity = this.getById(id);
        return converter.convert(entity, AiQuotaGiftVO.class);
    }
}
