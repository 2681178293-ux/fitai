package net.wanho.fitai.modules.coach.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBindAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBindQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBindUpdateDTO;
import net.wanho.fitai.modules.coach.model.po.UsrCoachBind;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachBindVO;

public interface UsrCoachBindServiceI extends MPJBaseService<UsrCoachBind> {
    IPage<UsrCoachBindVO> findByPage(UsrCoachBindQueryDTO queryDto);

    void add(UsrCoachBindAddDTO addDto);

    void update(UsrCoachBindUpdateDTO updateDto);

    void delete(String id);

    UsrCoachBindVO findById(Long id);

}
