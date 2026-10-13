package net.wanho.fitai.modules.member.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.member.mapper.UsrSettingMapper;
import net.wanho.fitai.modules.member.model.dto.UsrSettingAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSettingQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSettingUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrSetting;
import net.wanho.fitai.modules.member.model.vo.UsrSettingVO;
import net.wanho.fitai.modules.member.service.UsrSettingServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsrSettingServiceImpl extends MPJBaseServiceImpl<UsrSettingMapper, UsrSetting> implements UsrSettingServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrSettingVO> findByPage(UsrSettingQueryDTO queryDto) {

        IPage<UsrSetting> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<UsrSetting> wrapper = new LambdaQueryWrapper<>();
        IPage<UsrSetting> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, UsrSettingVO.class));

    }

    @Override
    public void add(UsrSettingAddDTO addDto) {
        UsrSetting usrsetting = converter.convert(addDto, UsrSetting.class);
        this.save(usrsetting);
    }

    @Override
    public void update(UsrSettingUpdateDTO updateDto) {
        UsrSetting usrsetting = converter.convert(updateDto, UsrSetting.class);
        this.updateById(usrsetting);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrSettingVO findById(Long id) {
        UsrSetting entity = this.getById(id);
        return converter.convert(entity, UsrSettingVO.class);
    }
}
