package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemSubscriptionMapper;
import net.wanho.fitai.modules.member.model.dto.MemSubscriptionAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemSubscriptionQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemSubscriptionUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemSubscription;
import net.wanho.fitai.modules.member.model.po.MemberPlan;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import net.wanho.fitai.modules.member.model.vo.MemSubscriptionVO;
import net.wanho.fitai.modules.member.service.MemSubscriptionServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemSubscriptionServiceImpl extends MPJBaseServiceImpl<MemSubscriptionMapper, MemSubscription> implements MemSubscriptionServiceI {

    private final Converter converter;
    private final MemSubscriptionMapper memSubscriptionMapper;

    @Override
    public IPage<MemSubscriptionVO> findByPage(MemSubscriptionQueryDTO queryDto) {

        IPage<MemSubscriptionVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        MPJLambdaWrapper<MemSubscription> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(MemSubscription.class);
        wrapper.selectAs(UsrCustomer::getNickName, MemSubscriptionVO::getNickname);
        wrapper.selectAs(MemberPlan::getName, MemSubscriptionVO::getPackageName);
        wrapper.selectAs(MemberPlan::getPrice, MemSubscriptionVO::getPrice);
        wrapper.leftJoin(UsrCustomer.class, UsrCustomer::getCustomerId, MemSubscription::getCustomerId);
        wrapper.leftJoin(MemberPlan.class, MemberPlan::getId, MemSubscription::getPackageId);

        wrapper.eq(queryDto.getCustomerId() != null, UsrCustomer::getCustomerId, queryDto.getCustomerId());
        wrapper.like(StrUtil.isNotBlank(queryDto.getPackageName()), MemSubscription::getPackageName, queryDto.getPackageName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), MemSubscription::getStatus, queryDto.getStatus());

        return memSubscriptionMapper.selectJoinPage(page, MemSubscriptionVO.class,wrapper);

    }

    @Override
    public void add(MemSubscriptionAddDTO addDto) {
        MemSubscription memsubscription = converter.convert(addDto, MemSubscription.class);
        this.save(memsubscription);
    }

    @Override
    public void update(MemSubscriptionUpdateDTO updateDto) {
        MemSubscription memsubscription = converter.convert(updateDto, MemSubscription.class);
        this.updateById(memsubscription);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemSubscriptionVO findById(Long id) {
        MemSubscription entity = this.getById(id);
        return converter.convert(entity, MemSubscriptionVO.class);
    }
}
