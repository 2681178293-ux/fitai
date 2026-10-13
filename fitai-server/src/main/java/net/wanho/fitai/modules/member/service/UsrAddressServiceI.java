package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.UsrAddressAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrAddressQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrAddressUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrAddress;
import net.wanho.fitai.modules.member.model.vo.UsrAddressVO;

public interface UsrAddressServiceI extends MPJBaseService<UsrAddress> {
    IPage<UsrAddressVO> findByPage(UsrAddressQueryDTO queryDto);

    void add(UsrAddressAddDTO addDto);

    void update(UsrAddressUpdateDTO updateDto);

    void delete(String id);

    UsrAddressVO findById(Long id);

}
