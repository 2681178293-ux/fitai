package net.wanho.fitai.modules.marketing.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.marketing.mapper.MktChallengeRecordMapper;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeRecordAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeRecordQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeRecordUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.MktChallengeRecord;
import net.wanho.fitai.modules.marketing.model.vo.MktChallengeRecordVO;
import net.wanho.fitai.modules.marketing.service.MktChallengeRecordServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MktChallengeRecordServiceImpl extends MPJBaseServiceImpl<MktChallengeRecordMapper, MktChallengeRecord> implements MktChallengeRecordServiceI {

    private final Converter converter;

    @Override
    public IPage<MktChallengeRecordVO> findByPage(MktChallengeRecordQueryDTO queryDto) {

        IPage<MktChallengeRecord> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MktChallengeRecord> wrapper = new LambdaQueryWrapper<>();
        IPage<MktChallengeRecord> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MktChallengeRecordVO.class));

    }

    @Override
    public void add(MktChallengeRecordAddDTO addDto) {
        MktChallengeRecord mktchallengerecord = converter.convert(addDto, MktChallengeRecord.class);
        this.save(mktchallengerecord);
    }

    @Override
    public void update(MktChallengeRecordUpdateDTO updateDto) {
        MktChallengeRecord mktchallengerecord = converter.convert(updateDto, MktChallengeRecord.class);
        this.updateById(mktchallengerecord);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MktChallengeRecordVO findById(Long id) {
        MktChallengeRecord entity = this.getById(id);
        return converter.convert(entity, MktChallengeRecordVO.class);
    }
}
