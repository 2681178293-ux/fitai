package net.wanho.fitai.modules.course.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.course.mapper.CrsVideoMapper;
import net.wanho.fitai.modules.course.model.dto.CrsVideoAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsVideoQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsVideoUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsVideo;
import net.wanho.fitai.modules.course.model.vo.CrsVideoVO;
import net.wanho.fitai.modules.course.service.CrsVideoServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CrsVideoServiceImpl extends MPJBaseServiceImpl<CrsVideoMapper, CrsVideo> implements CrsVideoServiceI {

    private final Converter converter;

    @Override
    public IPage<CrsVideoVO> findByPage(CrsVideoQueryDTO queryDto) {

        IPage<CrsVideo> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CrsVideo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCourseTitle()), CrsVideo::getCourseTitle, queryDto.getCourseTitle());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getVideoTitle()), CrsVideo::getVideoTitle, queryDto.getVideoTitle());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getVodCategoryName()), CrsVideo::getVodCategoryName, queryDto.getVodCategoryName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), CrsVideo::getStatus, queryDto.getStatus());
        IPage<CrsVideo> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CrsVideoVO.class));

    }

    @Override
    public void add(CrsVideoAddDTO addDto) {
        CrsVideo crsvideo = converter.convert(addDto, CrsVideo.class);
        this.save(crsvideo);
    }

    @Override
    public void update(CrsVideoUpdateDTO updateDto) {
        CrsVideo crsvideo = converter.convert(updateDto, CrsVideo.class);
        this.updateById(crsvideo);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CrsVideoVO findById(Long id) {
        CrsVideo entity = this.getById(id);
        return converter.convert(entity, CrsVideoVO.class);
    }
}
