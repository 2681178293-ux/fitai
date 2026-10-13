package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.UsrSegmentMapper;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrSegment;
import net.wanho.fitai.modules.member.model.vo.UsrSegmentVO;
import net.wanho.fitai.modules.member.service.UsrSegmentServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsrSegmentServiceImpl extends MPJBaseServiceImpl<UsrSegmentMapper, UsrSegment> implements UsrSegmentServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrSegmentVO> findByPage(UsrSegmentQueryDTO queryDto) {

        IPage<UsrSegment> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<UsrSegment> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSegmentName()), UsrSegment::getSegmentName, queryDto.getSegmentName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getSegmentCode()), UsrSegment::getSegmentCode, queryDto.getSegmentCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), UsrSegment::getStatus, queryDto.getStatus());
        IPage<UsrSegment> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, UsrSegmentVO.class));

    }

    @Override
    public void add(UsrSegmentAddDTO addDto) {
        UsrSegment usrsegment = converter.convert(addDto, UsrSegment.class);
        this.save(usrsegment);
    }

    @Override
    public void update(UsrSegmentUpdateDTO updateDto) {
        UsrSegment usrsegment = converter.convert(updateDto, UsrSegment.class);
        this.updateById(usrsegment);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrSegmentVO findById(Long id) {
        UsrSegment entity = this.getById(id);
        return converter.convert(entity, UsrSegmentVO.class);
    }
}
