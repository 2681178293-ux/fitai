package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiGuardrailMapper;
import net.wanho.fitai.modules.ai.model.dto.AiGuardrailAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiGuardrailQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiGuardrailUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiGuardrail;
import net.wanho.fitai.modules.ai.model.vo.AiGuardrailVO;
import net.wanho.fitai.modules.ai.service.AiGuardrailServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiGuardrailServiceImpl extends MPJBaseServiceImpl<AiGuardrailMapper, AiGuardrail> implements AiGuardrailServiceI {

    private final Converter converter;

    @Override
    public IPage<AiGuardrailVO> findByPage(AiGuardrailQueryDTO queryDto) {

        IPage<AiGuardrail> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiGuardrail> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRuleName()), AiGuardrail::getRuleName, queryDto.getRuleName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), AiGuardrail::getStatus, queryDto.getStatus());
        IPage<AiGuardrail> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiGuardrailVO.class));

    }

    @Override
    public void add(AiGuardrailAddDTO addDto) {
        AiGuardrail aiguardrail = converter.convert(addDto, AiGuardrail.class);
        this.save(aiguardrail);
    }

    @Override
    public void update(AiGuardrailUpdateDTO updateDto) {
        AiGuardrail aiguardrail = converter.convert(updateDto, AiGuardrail.class);
        this.updateById(aiguardrail);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiGuardrailVO findById(Long id) {
        AiGuardrail entity = this.getById(id);
        return converter.convert(entity, AiGuardrailVO.class);
    }
}
