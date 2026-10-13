package net.wanho.fitai.modules.community.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.community.mapper.CommunityPostMapper;
import net.wanho.fitai.modules.community.model.dto.CommunityPostAddDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityPostQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityPostUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CommunityPost;
import net.wanho.fitai.modules.community.model.vo.CommunityPostVO;
import net.wanho.fitai.modules.community.service.CommunityPostServiceI;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl extends MPJBaseServiceImpl<CommunityPostMapper, CommunityPost> implements CommunityPostServiceI {

    private final Converter converter;

    @Override
    public IPage<CommunityPostVO> findByPage(CommunityPostQueryDTO queryDto) {

        IPage<CommunityPostVO> page = new Page<>();
        page.setSize(queryDto.getPageSize());
        page.setCurrent(queryDto.getPageNum());

        MPJLambdaWrapper<CommunityPost> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(CommunityPost.class)
                .selectAs(UsrCustomer::getNickName, CommunityPostVO::getUserNickname)
                .selectAs(UsrCustomer::getAvatar, CommunityPostVO::getUserAvatar)
                .leftJoin(UsrCustomer.class,UsrCustomer::getCustomerId,CommunityPost::getUserId)
                .like(StrUtil.isNotEmpty(queryDto.getKeyword()), CommunityPost::getContent, queryDto.getKeyword())
                .like(StrUtil.isNotEmpty(queryDto.getUserNickname()), UsrCustomer::getNickName, queryDto.getUserNickname())
                .eq(StrUtil.isNotEmpty(queryDto.getStatus()), CommunityPost::getStatus, queryDto.getStatus())
                .eq(StrUtil.isNotEmpty(queryDto.getType()), CommunityPost::getType, queryDto.getType())
                .orderByDesc(CommunityPost::getCreateTime);

        return this.baseMapper.selectJoinPage(page, CommunityPostVO.class, wrapper);
    }

    @Override
    public void add(CommunityPostAddDTO addDto) {
        CommunityPost communitypost = converter.convert(addDto, CommunityPost.class);
        this.save(communitypost);
    }

    @Override
    public void update(CommunityPostUpdateDTO updateDto) {
        CommunityPost communitypost = converter.convert(updateDto, CommunityPost.class);
        this.updateById(communitypost);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CommunityPostVO findById(Long id) {
        CommunityPost entity = this.getById(id);
        return converter.convert(entity, CommunityPostVO.class);
    }
}
