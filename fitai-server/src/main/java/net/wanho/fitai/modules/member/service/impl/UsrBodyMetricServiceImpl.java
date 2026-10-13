package net.wanho.fitai.modules.member.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.exception.CustomException;
import net.wanho.fitai.modules.member.code.MemberCode;
import net.wanho.fitai.modules.member.mapper.UsrBodyMetricMapper;
import net.wanho.fitai.modules.member.model.dto.UsrBodyMetricAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrBodyMetricQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrBodyMetricUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrBodyMetric;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import net.wanho.fitai.modules.member.model.vo.UsrBodyMetricKpiVO;
import net.wanho.fitai.modules.member.model.vo.UsrBodyMetricVO;
import net.wanho.fitai.modules.member.service.UsrBodyMetricServiceI;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsrBodyMetricServiceImpl extends MPJBaseServiceImpl<UsrBodyMetricMapper, UsrBodyMetric> implements UsrBodyMetricServiceI {

    private final Converter converter;

    @Override
    public IPage<UsrBodyMetricVO> findByPage(UsrBodyMetricQueryDTO queryDto) {
        IPage<UsrBodyMetricVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        MPJLambdaWrapper<UsrBodyMetric> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(UsrBodyMetric.class);
        wrapper.selectAs(UsrCustomer::getNickName, UsrBodyMetricVO::getNickName);
        wrapper.leftJoin(UsrCustomer.class, UsrCustomer::getCustomerId, UsrBodyMetric::getCustomerId);
        wrapper.eq(queryDto.getCustomerId() != null, UsrBodyMetric::getCustomerId, queryDto.getCustomerId());
        wrapper.ge(queryDto.getStartDate() != null, UsrBodyMetric::getRecordDate, queryDto.getStartDate());
        wrapper.le(queryDto.getEndDate() != null, UsrBodyMetric::getRecordDate, queryDto.getEndDate());
        wrapper.orderByDesc(UsrBodyMetric::getRecordDate);
        wrapper.orderByDesc(UsrBodyMetric::getMetricId);
        return baseMapper.selectJoinPage(page, UsrBodyMetricVO.class, wrapper);
    }

    @Override
    public UsrBodyMetricKpiVO getKpi() {
        List<UsrBodyMetric> metricList = this.list();

        // 记录总数：不去重
        Long recordTotal = (long) metricList.size();

        // 覆盖用户数：customerId 去重
        Long userCount = metricList.stream()
                .map(UsrBodyMetric::getCustomerId)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        // 本月新增记录数
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        Long monthNewCount = metricList.stream()
                .filter(item -> item.getRecordDate() != null && !item.getRecordDate().isBefore(monthStart))
                .count();

        // 平均体脂率：同一 customerId 只保留最近 recordDate 的体脂率，再求平均
        List<BigDecimal> avgBodyFatList = metricList.stream()
                .filter(item -> item.getCustomerId() != null
                        && item.getBodyFatPct() != null
                        && item.getRecordDate() != null)
                .collect(Collectors.toMap(
                        UsrBodyMetric::getCustomerId,
                        item -> item,
                        (oldVal, newVal) -> {
                            int cmp = newVal.getRecordDate().compareTo(oldVal.getRecordDate());
                            if (cmp != 0) {
                                return cmp > 0 ? newVal : oldVal;
                            }
                            Long oldId = oldVal.getMetricId() == null ? 0L : oldVal.getMetricId();
                            Long newId = newVal.getMetricId() == null ? 0L : newVal.getMetricId();
                            return newId >= oldId ? newVal : oldVal;
                        }
                ))
                .values()
                .stream()
                .map(UsrBodyMetric::getBodyFatPct)
                .toList();

        BigDecimal sumBodyFat = avgBodyFatList.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 除数或被除数为 0 时报错
        if (avgBodyFatList.isEmpty() || sumBodyFat.compareTo(BigDecimal.ZERO) == 0) {
            throw new CustomException(MemberCode.BODY_METRIC_KPI_CALC_ERROR);
        }
        
        BigDecimal avgBodyFat = sumBodyFat.divide(
                BigDecimal.valueOf(avgBodyFatList.size()), 2, RoundingMode.HALF_UP);

        UsrBodyMetricKpiVO vo = new UsrBodyMetricKpiVO();
        vo.setRecordTotal(recordTotal);
        vo.setUserCount(userCount);
        vo.setMonthNewCount(monthNewCount);
        vo.setAvgBodyFat(avgBodyFat);    return vo;

    }

    @Override
    public List<UsrBodyMetricVO> findTrend(Long customerId, Integer limit) {
        int size = (limit == null || limit <= 0) ? 30 : limit;
        LambdaQueryWrapper<UsrBodyMetric> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsrBodyMetric::getCustomerId, customerId);
        wrapper.orderByDesc(UsrBodyMetric::getRecordDate);
        wrapper.last("LIMIT " + size);
        return this.list(wrapper).stream()
                .map(item -> converter.convert(item, UsrBodyMetricVO.class))
                .toList();
    }

    @Override
    public void add(UsrBodyMetricAddDTO addDto) {
        UsrBodyMetric entity = converter.convert(addDto, UsrBodyMetric.class);
        this.save(entity);
    }

    @Override
    public void update(UsrBodyMetricUpdateDTO updateDto) {
        UsrBodyMetric entity = converter.convert(updateDto, UsrBodyMetric.class);
        this.updateById(entity);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public UsrBodyMetricVO findById(Long id) {
        UsrBodyMetric entity = this.getById(id);
        return converter.convert(entity, UsrBodyMetricVO.class);
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }
}
