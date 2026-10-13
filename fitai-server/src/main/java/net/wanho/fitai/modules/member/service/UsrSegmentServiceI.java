package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSegmentUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrSegment;
import net.wanho.fitai.modules.member.model.vo.UsrSegmentVO;

public interface UsrSegmentServiceI extends MPJBaseService<UsrSegment> {
    IPage<UsrSegmentVO> findByPage(UsrSegmentQueryDTO queryDto);

    void add(UsrSegmentAddDTO addDto);

    void update(UsrSegmentUpdateDTO updateDto);

    void delete(String id);

    UsrSegmentVO findById(Long id);

}
