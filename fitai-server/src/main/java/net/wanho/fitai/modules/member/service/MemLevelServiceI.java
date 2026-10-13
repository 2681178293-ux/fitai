package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemLevelAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemLevelUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemLevel;
import net.wanho.fitai.modules.member.model.vo.MemLevelVO;

public interface MemLevelServiceI extends MPJBaseService<MemLevel> {
    IPage<MemLevelVO> findByPage();

    void add(MemLevelAddDTO addDto);

    void update(MemLevelUpdateDTO updateDto);

    void delete(String id);

    MemLevelVO findById(Long id);

}
