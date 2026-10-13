package net.wanho.fitai.modules.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.ai.mapper.AiUserQuotaMapper;
import net.wanho.fitai.modules.ai.model.dto.AiUserQuotaAddDTO;
import net.wanho.fitai.modules.ai.model.dto.AiUserQuotaQueryDTO;
import net.wanho.fitai.modules.ai.model.dto.AiUserQuotaUpdateDTO;
import net.wanho.fitai.modules.ai.model.po.AiUserQuota;
import net.wanho.fitai.modules.ai.model.vo.AiUserQuotaVO;
import net.wanho.fitai.modules.ai.service.AiUserQuotaServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiUserQuotaServiceImpl extends MPJBaseServiceImpl<AiUserQuotaMapper, AiUserQuota> implements AiUserQuotaServiceI {

    private final Converter converter;

    @Override
    public IPage<AiUserQuotaVO> findByPage(AiUserQuotaQueryDTO queryDto) {

        IPage<AiUserQuota> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<AiUserQuota> wrapper = new LambdaQueryWrapper<>();
        IPage<AiUserQuota> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, AiUserQuotaVO.class));

    }

    @Override
    public void add(AiUserQuotaAddDTO addDto) {
        AiUserQuota aiuserquota = converter.convert(addDto, AiUserQuota.class);
        this.save(aiuserquota);
    }

    @Override
    public void update(AiUserQuotaUpdateDTO updateDto) {
        AiUserQuota aiuserquota = converter.convert(updateDto, AiUserQuota.class);
        this.updateById(aiuserquota);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public AiUserQuotaVO findById(Long id) {
        AiUserQuota entity = this.getById(id);
        return converter.convert(entity, AiUserQuotaVO.class);
    }
}
