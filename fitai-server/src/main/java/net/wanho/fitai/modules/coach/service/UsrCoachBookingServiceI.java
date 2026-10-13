package net.wanho.fitai.modules.coach.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBookingAddDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBookingQueryDTO;
import net.wanho.fitai.modules.coach.model.dto.UsrCoachBookingUpdateDTO;
import net.wanho.fitai.modules.coach.model.po.UsrCoachBooking;
import net.wanho.fitai.modules.coach.model.vo.UsrCoachBookingVO;

public interface UsrCoachBookingServiceI extends MPJBaseService<UsrCoachBooking> {
    IPage<UsrCoachBookingVO> findByPage(UsrCoachBookingQueryDTO queryDto);

    void add(UsrCoachBookingAddDTO addDto);

    void update(UsrCoachBookingUpdateDTO updateDto);

    void delete(String id);

    UsrCoachBookingVO findById(Long id);

}
