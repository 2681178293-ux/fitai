package net.wanho.fitai.modules.member.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemCheckinMapper;
import net.wanho.fitai.modules.member.model.dto.MemCheckinAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemCheckinQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemCheckinUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemCheckin;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import net.wanho.fitai.modules.member.model.vo.MemCheckinVO;
import net.wanho.fitai.modules.member.service.MemCheckinServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemCheckinServiceImpl extends MPJBaseServiceImpl<MemCheckinMapper, MemCheckin> implements MemCheckinServiceI {

    private final Converter converter;
    private final MemCheckinMapper memCheckinMapper;

    @Override
    public IPage<MemCheckinVO> findByPage(MemCheckinQueryDTO queryDto) {

        IPage<MemCheckinVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        MPJLambdaWrapper<MemCheckin> wrapper = new MPJLambdaWrapper<MemCheckin>();
        wrapper.selectAll(MemCheckin.class);
        wrapper.eq(queryDto.getCustomerId() != null, MemCheckin::getCustomerId, queryDto.getCustomerId());
        wrapper.selectAs(UsrCustomer::getNickName, MemCheckinVO::getNickName);
        wrapper.leftJoin(UsrCustomer.class, UsrCustomer::getCustomerId,MemCheckin::getCustomerId);
        wrapper.orderByAsc(MemCheckin::getCreateTime);
        return memCheckinMapper.selectJoinPage(page, MemCheckinVO.class, wrapper);


    }

    @Override
    public void add(MemCheckinAddDTO addDto) {
        MemCheckin memcheckin = converter.convert(addDto, MemCheckin.class);
        this.save(memcheckin);
    }

    @Override
    public void update(MemCheckinUpdateDTO updateDto) {
        MemCheckin memcheckin = converter.convert(updateDto, MemCheckin.class);
        this.updateById(memcheckin);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemCheckinVO findById(Long id) {
        MemCheckin entity = this.getById(id);
        return converter.convert(entity, MemCheckinVO.class);
    }
}
