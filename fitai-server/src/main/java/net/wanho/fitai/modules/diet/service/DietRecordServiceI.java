package net.wanho.fitai.modules.diet.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.diet.model.dto.DietRecordAddDTO;
import net.wanho.fitai.modules.diet.model.dto.DietRecordQueryDTO;
import net.wanho.fitai.modules.diet.model.dto.DietRecordUpdateDTO;
import net.wanho.fitai.modules.diet.model.po.DietRecord;
import net.wanho.fitai.modules.diet.model.vo.DietRecordVO;

public interface DietRecordServiceI extends MPJBaseService<DietRecord> {
    IPage<DietRecordVO> findByPage(DietRecordQueryDTO queryDto);

    void add(DietRecordAddDTO addDto);

    void update(DietRecordUpdateDTO updateDto);

    void delete(String id);

    DietRecordVO findById(Long id);

}
