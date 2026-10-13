package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.MktChallengeMapper;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktChallenge;
import net.wanho.fitai.modules.marketing.model.vo.MktChallengeVO;
import net.wanho.fitai.modules.marketing.service.MktChallengeServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MktChallengeServiceImpl extends MPJBaseServiceImpl<MktChallengeMapper, MktChallenge> implements MktChallengeServiceI {

    private final Converter converter;

    @Override
    public IPage<MktChallengeVO> findByPage(MktChallengeQueryDTO queryDto) {

        IPage<MktChallenge> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MktChallenge> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getName()), MktChallenge::getName, queryDto.getName());
        IPage<MktChallenge> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MktChallengeVO.class));

    }

    @Override
    public void add(MktChallengeAddDTO addDto) {
        MktChallenge mktchallenge = converter.convert(addDto, MktChallenge.class);
        this.save(mktchallenge);
    }

    @Override
    public void update(MktChallengeUpdateDTO updateDto) {
        MktChallenge mktchallenge = converter.convert(updateDto, MktChallenge.class);
        this.updateById(mktchallenge);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MktChallengeVO findById(Long id) {
        MktChallenge entity = this.getById(id);
        return converter.convert(entity, MktChallengeVO.class);
    }
}
