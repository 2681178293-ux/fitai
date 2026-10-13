package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.UsrBodyMetricAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrBodyMetricQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrBodyMetricUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrBodyMetric;
import net.wanho.fitai.modules.member.model.vo.UsrBodyMetricKpiVO;
import net.wanho.fitai.modules.member.model.vo.UsrBodyMetricVO;

import java.util.List;

public interface UsrBodyMetricServiceI extends MPJBaseService<UsrBodyMetric> {
    IPage<UsrBodyMetricVO> findByPage(UsrBodyMetricQueryDTO queryDto);

    UsrBodyMetricKpiVO getKpi();

    List<UsrBodyMetricVO> findTrend(Long customerId, Integer limit);

    void add(UsrBodyMetricAddDTO addDto);

    void update(UsrBodyMetricUpdateDTO updateDto);

    void delete(String id);

    UsrBodyMetricVO findById(Long id);

}
