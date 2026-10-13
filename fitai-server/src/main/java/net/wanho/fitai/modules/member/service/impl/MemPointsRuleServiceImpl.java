package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemPointsRuleMapper;
import net.wanho.fitai.modules.member.model.dto.MemPointsRuleAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsRuleQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemPointsRuleUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemPointsRule;
import net.wanho.fitai.modules.member.model.vo.MemPointsRuleVO;
import net.wanho.fitai.modules.member.service.MemPointsRuleServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemPointsRuleServiceImpl extends MPJBaseServiceImpl<MemPointsRuleMapper, MemPointsRule> implements MemPointsRuleServiceI {

    private final Converter converter;

    @Override
    public IPage<MemPointsRuleVO> findByPage(MemPointsRuleQueryDTO queryDto) {

        IPage<MemPointsRule> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MemPointsRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getRuleName()), MemPointsRule::getRuleName, queryDto.getRuleName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getEventType()), MemPointsRule::getEventType, queryDto.getEventType());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), MemPointsRule::getStatus, queryDto.getStatus());
        IPage<MemPointsRule> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MemPointsRuleVO.class));
    }

    @Override
    public void add(MemPointsRuleAddDTO addDto) {
        MemPointsRule mempointsrule = converter.convert(addDto, MemPointsRule.class);
        this.save(mempointsrule);
    }

    @Override
    public void update(MemPointsRuleUpdateDTO updateDto) {
        MemPointsRule mempointsrule = converter.convert(updateDto, MemPointsRule.class);
        this.updateById(mempointsrule);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemPointsRuleVO findById(Long id) {
        MemPointsRule entity = this.getById(id);
        return converter.convert(entity, MemPointsRuleVO.class);
    }
}
