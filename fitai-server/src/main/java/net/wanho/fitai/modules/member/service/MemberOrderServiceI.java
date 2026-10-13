package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemberOrderAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemberOrderQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemberOrderUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemberOrder;
import net.wanho.fitai.modules.member.model.vo.MemberOrderVO;

public interface MemberOrderServiceI extends MPJBaseService<MemberOrder> {
    IPage<MemberOrderVO> findByPage(MemberOrderQueryDTO queryDto);

    void add(MemberOrderAddDTO addDto);

    void update(MemberOrderUpdateDTO updateDto);

    void delete(String id);

    MemberOrderVO findById(Long id);

}
