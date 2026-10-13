package net.wanho.fitai.modules.course.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.course.model.dto.CrsCourseAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsCourse;
import net.wanho.fitai.modules.course.model.vo.CrsCourseVO;

public interface CrsCourseServiceI extends MPJBaseService<CrsCourse> {
    IPage<CrsCourseVO> findByPage(CrsCourseQueryDTO queryDto);

    void add(CrsCourseAddDTO addDto);

    void update(CrsCourseUpdateDTO updateDto);

    void delete(String id);

    CrsCourseVO findById(Long id);

}
