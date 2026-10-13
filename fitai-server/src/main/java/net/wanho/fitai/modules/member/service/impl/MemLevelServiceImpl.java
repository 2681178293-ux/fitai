package net.wanho.fitai.modules.member.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.MemLevelMapper;
import net.wanho.fitai.modules.member.model.dto.MemLevelAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemLevelUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemLevel;
import net.wanho.fitai.modules.member.model.vo.MemLevelVO;
import net.wanho.fitai.modules.member.service.MemLevelServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MemLevelServiceImpl extends MPJBaseServiceImpl<MemLevelMapper, MemLevel> implements MemLevelServiceI {

    private final Converter converter;

    @Override
    public IPage<MemLevelVO> findByPage() {
        LambdaQueryWrapper<MemLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MemLevel::getCreateTime);
        wrapper.orderByDesc(MemLevel::getOrderNum);
        return this.page(new Page<>(), wrapper)
                .convert(item -> converter.convert(item, MemLevelVO.class));

    }

    @Override
    public void add(MemLevelAddDTO addDto) {
        MemLevel memlevel = converter.convert(addDto, MemLevel.class);
        this.save(memlevel);
    }

    @Override
    public void update(MemLevelUpdateDTO updateDto) {
        MemLevel memlevel = converter.convert(updateDto, MemLevel.class);
        this.updateById(memlevel);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MemLevelVO findById(Long id) {
        MemLevel entity = this.getById(id);
        return converter.convert(entity, MemLevelVO.class);
    }
}
