package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.UsrSegmentRelMapper;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentRelAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentRelQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentRelUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrSegmentRel;
import net.wanho.fitai.modules.member.model.vo.UsrSegmentRelVO;
import net.wanho.fitai.modules.member.service.UsrSegmentRelServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsrSegmentRelServiceImpl extends MPJBaseServiceImpl<UsrSegmentRelMapper, UsrSegmentRel> implements UsrSegmentRelServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrSegmentRelVO> findByPage(UsrSegmentRelQueryDTO queryDto) {

        IPage<UsrSegmentRel> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<UsrSegmentRel> wrapper = new LambdaQueryWrapper<>();
        IPage<UsrSegmentRel> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, UsrSegmentRelVO.class));

    }

    @Override
    public void add(UsrSegmentRelAddDTO addDto) {
        UsrSegmentRel usrsegmentrel = converter.convert(addDto, UsrSegmentRel.class);
        this.save(usrsegmentrel);
    }

    @Override
    public void update(UsrSegmentRelUpdateDTO updateDto) {
        UsrSegmentRel usrsegmentrel = converter.convert(updateDto, UsrSegmentRel.class);
        this.updateById(usrsegmentrel);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrSegmentRelVO findById(Long id) {
        UsrSegmentRel entity = this.getById(id);
        return converter.convert(entity, UsrSegmentRelVO.class);
    }
}
