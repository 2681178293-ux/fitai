package net.wanho.fitai.modules.course.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.course.model.dto.CrsVideoAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsVideoQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsVideoUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsVideo;
import net.wanho.fitai.modules.course.model.vo.CrsVideoVO;

public interface CrsVideoServiceI extends MPJBaseService<CrsVideo> {
    IPage<CrsVideoVO> findByPage(CrsVideoQueryDTO queryDto);

    void add(CrsVideoAddDTO addDto);

    void update(CrsVideoUpdateDTO updateDto);

    void delete(String id);

    CrsVideoVO findById(Long id);

}
