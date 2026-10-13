package net.wanho.fitai.modules.community.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.community.mapper.CommunityCommentMapper;
import net.wanho.fitai.modules.community.model.dto.CommunityCommentAddDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityCommentQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CommunityCommentUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CommunityComment;
import net.wanho.fitai.modules.community.model.vo.CommunityCommentVO;
import net.wanho.fitai.modules.community.service.CommunityCommentServiceI;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CommunityCommentServiceImpl extends MPJBaseServiceImpl<CommunityCommentMapper, CommunityComment> implements CommunityCommentServiceI {

    private final Converter converter;

    @Override
    public IPage<CommunityCommentVO> findByPage(CommunityCommentQueryDTO queryDto) {
        IPage<CommunityCommentVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        MPJLambdaWrapper<CommunityComment> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(CommunityComment.class);
        wrapper.selectAs(UsrCustomer::getNickName, CommunityCommentVO::getNickname);
        wrapper.leftJoin(UsrCustomer.class, UsrCustomer::getCustomerId, CommunityComment::getUserId);
        wrapper.like(StrUtil.isNotEmpty(queryDto.getKeyword()), CommunityComment::getContent, queryDto.getKeyword());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getUserNickname()), UsrCustomer::getNickName, queryDto.getUserNickname());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), CommunityComment::getStatus, queryDto.getStatus());
        wrapper.orderByDesc(CommunityComment::getCreateTime);
        return this.selectJoinListPage(page,CommunityCommentVO.class,wrapper);

    }

    @Override
    public void top(Long id, Integer top) {
        CommunityComment communityComment =new CommunityComment();
        communityComment.setId(id);
        communityComment.setTop(top);
        this.updateById(communityComment);
    }





    @Override
    public void add(CommunityCommentAddDTO addDto) {
        CommunityComment communitycomment = converter.convert(addDto, CommunityComment.class);
        this.save(communitycomment);
    }

    @Override
    public void update(CommunityCommentUpdateDTO updateDto) {
        CommunityComment communitycomment = converter.convert(updateDto, CommunityComment.class);
        this.updateById(communitycomment);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CommunityCommentVO findById(Long id) {
        CommunityComment entity = this.getById(id);
        return converter.convert(entity, CommunityCommentVO.class);
    }


}
