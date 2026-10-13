package net.wanho.fitai.modules.marketing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.marketing.model.dto.SeckillActivityAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillActivityQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillActivityUpdateDTO;
import net.wanho.fitai.modules.marketing.model.po.SeckillActivity;
import net.wanho.fitai.modules.marketing.model.vo.SeckillActivityVO;

public interface SeckillActivityServiceI extends MPJBaseService<SeckillActivity> {
    IPage<SeckillActivityVO> findByPage(SeckillActivityQueryDTO queryDto);

    void add(SeckillActivityAddDTO addDto);

    void update(SeckillActivityUpdateDTO updateDto);

    void delete(String id);

    SeckillActivityVO findById(Long id);

}
