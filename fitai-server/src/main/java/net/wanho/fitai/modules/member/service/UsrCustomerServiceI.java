package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.UsrCustomerAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrCustomerQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrCustomerUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import net.wanho.fitai.modules.member.model.vo.UsrCustomerVO;

public interface UsrCustomerServiceI extends MPJBaseService<UsrCustomer> {
    IPage<UsrCustomerVO> findByPage(UsrCustomerQueryDTO queryDto);

    void add(UsrCustomerAddDTO addDto);

    void update(UsrCustomerUpdateDTO updateDto);

    void delete(String id);

    UsrCustomerVO findById(Long id);

}
