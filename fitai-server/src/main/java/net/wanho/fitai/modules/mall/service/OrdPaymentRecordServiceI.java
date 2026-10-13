package net.wanho.fitai.modules.mall.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentRecordAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentRecordQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdPaymentRecordUpdateDTO;
import net.wanho.fitai.modules.mall.model.po.OrdPaymentRecord;
import net.wanho.fitai.modules.mall.model.vo.OrdPaymentRecordVO;

public interface OrdPaymentRecordServiceI extends MPJBaseService<OrdPaymentRecord> {
    IPage<OrdPaymentRecordVO> findByPage(OrdPaymentRecordQueryDTO queryDto);

    void add(OrdPaymentRecordAddDTO addDto);

    void update(OrdPaymentRecordUpdateDTO updateDto);

    void delete(String id);

    OrdPaymentRecordVO findById(Long id);

}
