package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.MemPackageAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemPackageQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemPackageUpdateDTO;
import net.wanho.fitai.modules.member.model.po.MemPackage;
import net.wanho.fitai.modules.member.model.vo.MemPackageVO;

public interface MemPackageServiceI extends MPJBaseService<MemPackage> {
    IPage<MemPackageVO> findByPage(MemPackageQueryDTO queryDto);

    void add(MemPackageAddDTO addDto);

    void update(MemPackageUpdateDTO updateDto);

    void delete(String id);

    MemPackageVO findById(Long id);

}
