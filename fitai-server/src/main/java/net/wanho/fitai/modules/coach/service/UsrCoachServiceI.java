package net.wanho.fitai.modules.coach.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachUpdateDTO;
import net.wanho.fitai.modules.coach.model.po.UsrCoach;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachVO;

public interface UsrCoachServiceI extends MPJBaseService<UsrCoach> {
    IPage<UsrCoachVO> findByPage(UsrCoachQueryDTO queryDto);

    void add(UsrCoachAddDTO addDto);

    void update(UsrCoachUpdateDTO updateDto);

    void delete(String id);

    UsrCoachVO findById(Long id);

}
