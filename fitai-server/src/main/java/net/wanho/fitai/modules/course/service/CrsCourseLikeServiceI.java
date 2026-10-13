package net.wanho.fitai.modules.course.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.course.model.dto.CrsCourseLikeAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseLikeQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCourseLikeUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsCourseLike;
import net.wanho.fitai.modules.course.model.vo.CrsCourseLikeVO;

public interface CrsCourseLikeServiceI extends MPJBaseService<CrsCourseLike> {
    IPage<CrsCourseLikeVO> findByPage(CrsCourseLikeQueryDTO queryDto);

    void add(CrsCourseLikeAddDTO addDto);

    void update(CrsCourseLikeUpdateDTO updateDto);

    void delete(String id);

    CrsCourseLikeVO findById(Long id);

}
