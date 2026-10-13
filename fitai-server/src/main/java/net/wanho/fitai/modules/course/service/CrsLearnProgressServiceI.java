package net.wanho.fitai.modules.course.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.course.model.dto.CrsLearnProgressAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsLearnProgressQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsLearnProgressUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsLearnProgress;
import net.wanho.fitai.modules.course.model.vo.CrsLearnProgressStatVO;
import net.wanho.fitai.modules.course.model.vo.CrsLearnProgressVO;

import java.util.List;

public interface CrsLearnProgressServiceI extends MPJBaseService<CrsLearnProgress> {
    IPage<CrsLearnProgressVO> findByPage(CrsLearnProgressQueryDTO queryDto);

    void add(CrsLearnProgressAddDTO addDto);

    void update(CrsLearnProgressUpdateDTO updateDto);

    void delete(String id);

    CrsLearnProgressVO findById(Long id);

    List<CrsLearnProgressVO> findStudentsByCourseId(Long courseId);

    CrsLearnProgressStatVO stats(Long courseId);
}
