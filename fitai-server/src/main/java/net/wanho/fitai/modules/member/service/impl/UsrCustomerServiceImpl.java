package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.UsrCustomerMapper;
import net.wanho.fitai.modules.member.model.dto.UsrCustomerAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrCustomerQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrCustomerUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import net.wanho.fitai.modules.member.model.vo.UsrCustomerVO;
import net.wanho.fitai.modules.member.service.UsrCustomerServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsrCustomerServiceImpl extends MPJBaseServiceImpl<UsrCustomerMapper, UsrCustomer> implements UsrCustomerServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrCustomerVO> findByPage(UsrCustomerQueryDTO queryDto) {

        IPage<UsrCustomer> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<UsrCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getNickName()), UsrCustomer::getNickName, queryDto.getNickName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getMobile()), UsrCustomer::getMobile, queryDto.getMobile());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), UsrCustomer::getStatus, queryDto.getStatus());
        IPage<UsrCustomer> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, UsrCustomerVO.class));

    }

    @Override
    public void add(UsrCustomerAddDTO addDto) {
        UsrCustomer usrcustomer = converter.convert(addDto, UsrCustomer.class);
        this.save(usrcustomer);
    }

    @Override
    public void update(UsrCustomerUpdateDTO updateDto) {
        UsrCustomer usrcustomer = converter.convert(updateDto, UsrCustomer.class);
        this.updateById(usrcustomer);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrCustomerVO findById(Long id) {
        UsrCustomer entity = this.getById(id);
        return converter.convert(entity, UsrCustomerVO.class);
    }
}
