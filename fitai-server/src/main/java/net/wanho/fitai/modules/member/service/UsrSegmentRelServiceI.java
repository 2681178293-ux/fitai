package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentRelAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentRelQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentRelUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrSegmentRel;
import net.wanho.fitai.modules.member.model.vo.UsrSegmentRelVO;

public interface UsrSegmentRelServiceI extends MPJBaseService<UsrSegmentRel> {
    IPage<UsrSegmentRelVO> findByPage(UsrSegmentRelQueryDTO queryDto);

    void add(UsrSegmentRelAddDTO addDto);

    void update(UsrSegmentRelUpdateDTO updateDto);

    void delete(String id);

    UsrSegmentRelVO findById(Long id);

}
