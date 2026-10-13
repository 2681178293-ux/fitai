package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.UsrAddressMapper;
import net.wanho.fitai.modules.member.model.dto.UsrAddressAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrAddressQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrAddressUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrAddress;
import net.wanho.fitai.modules.member.model.vo.UsrAddressVO;
import net.wanho.fitai.modules.member.service.UsrAddressServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsrAddressServiceImpl extends MPJBaseServiceImpl<UsrAddressMapper, UsrAddress> implements UsrAddressServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrAddressVO> findByPage(UsrAddressQueryDTO queryDto) {

        IPage<UsrAddress> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<UsrAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getReceiverName()), UsrAddress::getReceiverName, queryDto.getReceiverName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getReceiverPhone()), UsrAddress::getReceiverPhone, queryDto.getReceiverPhone());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getPostalCode()), UsrAddress::getPostalCode, queryDto.getPostalCode());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), UsrAddress::getStatus, queryDto.getStatus());
        IPage<UsrAddress> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, UsrAddressVO.class));

    }

    @Override
    public void add(UsrAddressAddDTO addDto) {
        UsrAddress usraddress = converter.convert(addDto, UsrAddress.class);
        this.save(usraddress);
    }

    @Override
    public void update(UsrAddressUpdateDTO updateDto) {
        UsrAddress usraddress = converter.convert(updateDto, UsrAddress.class);
        this.updateById(usraddress);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrAddressVO findById(Long id) {
        UsrAddress entity = this.getById(id);
        return converter.convert(entity, UsrAddressVO.class);
    }
}
