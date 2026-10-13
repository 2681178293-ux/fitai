package net.wanho.fitai.modules.coach.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachScheduleAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachScheduleQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachScheduleUpdateDTO;
import net.wanho.fitai.modules.coach.model.po.UsrCoachSchedule;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachScheduleVO;

public interface UsrCoachScheduleServiceI extends MPJBaseService<UsrCoachSchedule> {
    IPage<UsrCoachScheduleVO> findByPage(UsrCoachScheduleQueryDTO queryDto);

    void add(UsrCoachScheduleAddDTO addDto);

    void update(UsrCoachScheduleUpdateDTO updateDto);

    void delete(String id);

    UsrCoachScheduleVO findById(Long id);

}
