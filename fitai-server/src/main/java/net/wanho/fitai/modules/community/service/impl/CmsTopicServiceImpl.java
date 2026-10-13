package net.wanho.fitai.modules.community.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.community.mapper.CmsTopicMapper;
import net.wanho.fitai.modules.community.model.dto.CmsTopicAddDTO;
import net.wanho.fitai.modules.community.model.dto.CmsTopicQueryDTO;
import net.wanho.fitai.modules.community.model.dto.CmsTopicUpdateDTO;
import net.wanho.fitai.modules.community.model.po.CmsTopic;
import net.wanho.fitai.modules.community.model.vo.CmsTopicVO;
import net.wanho.fitai.modules.community.service.CmsTopicServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CmsTopicServiceImpl extends MPJBaseServiceImpl<CmsTopicMapper, CmsTopic> implements CmsTopicServiceI {

    private final Converter converter;

    @Override
    public IPage<CmsTopicVO> findByPage(CmsTopicQueryDTO queryDto) {

        IPage<CmsTopic> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CmsTopic> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getTopicName()), CmsTopic::getTopicName, queryDto.getTopicName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), CmsTopic::getStatus, queryDto.getStatus());
        IPage<CmsTopic> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CmsTopicVO.class));

    }

    @Override
    public void add(CmsTopicAddDTO addDto) {
        CmsTopic cmstopic = converter.convert(addDto, CmsTopic.class);
        this.save(cmstopic);
    }

    @Override
    public void update(CmsTopicUpdateDTO updateDto) {
        CmsTopic cmstopic = converter.convert(updateDto, CmsTopic.class);
        this.updateById(cmstopic);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CmsTopicVO findById(Long id) {
        CmsTopic entity = this.getById(id);
        return converter.convert(entity, CmsTopicVO.class);
    }
}
