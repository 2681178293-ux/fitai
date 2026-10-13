package net.wanho.fitai.modules.course.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.course.mapper.CrsCourseLikeMapper;
import net.wanho.fitai.modules.course.model.dto.CrsCourseLikeAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseLikeQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseLikeUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsCourseLike;
import net.wanho.fitai.modules.course.model.vo.CrsCourseLikeVO;
import net.wanho.fitai.modules.course.service.CrsCourseLikeServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CrsCourseLikeServiceImpl extends MPJBaseServiceImpl<CrsCourseLikeMapper, CrsCourseLike> implements CrsCourseLikeServiceI {

    private final Converter converter;

    @Override
    public IPage<CrsCourseLikeVO> findByPage(CrsCourseLikeQueryDTO queryDto) {

        IPage<CrsCourseLike> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CrsCourseLike> wrapper = new LambdaQueryWrapper<>();
        IPage<CrsCourseLike> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CrsCourseLikeVO.class));

    }

    @Override
    public void add(CrsCourseLikeAddDTO addDto) {
        CrsCourseLike crscourselike = converter.convert(addDto, CrsCourseLike.class);
        this.save(crscourselike);
    }

    @Override
    public void update(CrsCourseLikeUpdateDTO updateDto) {
        CrsCourseLike crscourselike = converter.convert(updateDto, CrsCourseLike.class);
        this.updateById(crscourselike);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public CrsCourseLikeVO findById(Long id) {
        CrsCourseLike entity = this.getById(id);
        return converter.convert(entity, CrsCourseLikeVO.class);
    }
}
