package net.wanho.fitai.modules.course.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.course.model.dto.CrsCategoryAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCategoryQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCategoryUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsCategory;
import net.wanho.fitai.modules.course.model.vo.CrsCategoryVO;

import java.util.List;

public interface CrsCategoryServiceI extends MPJBaseService<CrsCategory> {
    IPage<CrsCategoryVO> findByPage(CrsCategoryQueryDTO queryDto);

    void add(CrsCategoryAddDTO addDto);

    void update(CrsCategoryUpdateDTO updateDto);

    void delete(String id);

    CrsCategoryVO findById(Long id);

    List<CrsCategoryVO> tree();
}
