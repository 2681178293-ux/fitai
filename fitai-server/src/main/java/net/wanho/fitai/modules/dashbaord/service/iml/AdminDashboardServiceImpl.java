package net.wanho.fitai.modules.dashbaord.service.iml;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.modules.course.mapper.CrsCourseMapper;
import net.wanho.fitai.modules.dashbaord.model.vo.DashBoardActivitiesVO;
import net.wanho.fitai.modules.dashbaord.model.vo.DashBoardDistributionVO;
import net.wanho.fitai.modules.dashbaord.model.vo.DashBoardTrendVO;
import net.wanho.fitai.modules.dashbaord.model.vo.DashboardStatsVO;
import net.wanho.fitai.modules.dashbaord.service.AdminDashboardServiceI;
import net.wanho.fitai.modules.mall.mapper.OrdOrderMapper;
import net.wanho.fitai.modules.mall.model.po.OrdOrder;
import net.wanho.fitai.modules.member.mapper.UsrCustomerMapper;
import net.wanho.fitai.modules.member.model.po.UsrCustomer;
import net.wanho.fitai.modules.system.mapper.SysLoginLogMapper;
import net.wanho.fitai.modules.system.model.po.SysLoginLog;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardServiceI {

    private final UsrCustomerMapper usrCustomerMapper;
    private final OrdOrderMapper ordOrderMapper;
    private final CrsCourseMapper crsCourseMapper;
    private final SysLoginLogMapper sysLoginLogMapper;

    private Long countByClientType(String clientType){
        return sysLoginLogMapper.selectCount(new LambdaQueryWrapper<SysLoginLog>().eq(SysLoginLog::getClientType,clientType));
    }


    private Long countUserByClientType(String clientType){
        QueryWrapper<SysLoginLog> loginLogWrapper = new QueryWrapper();
        loginLogWrapper.select("count(DISTINCT user_name)");
        loginLogWrapper.eq("client_type",clientType);
        List<Object> objs = sysLoginLogMapper.selectObjs(loginLogWrapper);
        return  Long.parseLong(objs.get(0).toString());
    }

    private Long countUserBoth(){
        QueryWrapper<SysLoginLog> loginLogWrapper = new QueryWrapper();
        loginLogWrapper.select("count(DISTINCT user_name)");
        List<Object> objs = sysLoginLogMapper.selectObjs(loginLogWrapper);
        return  Long.parseLong(objs.get(0).toString());
    }

    private final List<String> PAID_STATUSES= List.of("1","2","3");

    @Override
    public DashboardStatsVO getStats() {

        Long userCount = usrCustomerMapper.selectCount(null);
        Long orderCount = ordOrderMapper.selectCount(null);
        Long courseCount = crsCourseMapper.selectCount(null);


        /*SELECT COALESCE(sum(pay_amount),0) from ord_order where status in (1,2,3)*/
        QueryWrapper<OrdOrder> wrapper = new QueryWrapper();
        wrapper.select("COALESCE(sum(pay_amount),0)");
        wrapper.in("status",PAID_STATUSES);
        List<Object> objs = ordOrderMapper.selectObjs(wrapper);




        DashboardStatsVO dashboardStatsVO = new DashboardStatsVO();
        dashboardStatsVO.setUserCount(userCount);
        dashboardStatsVO.setOrderCount(orderCount);
        dashboardStatsVO.setSalesAmount(new BigDecimal(objs.get(0).toString()));
        dashboardStatsVO.setCourseCount(courseCount);


        DashboardStatsVO.ClientLoginStats clientLoginStats = new DashboardStatsVO.ClientLoginStats();
        clientLoginStats.setPcDistinctUsers(countUserByClientType("admin"));
        clientLoginStats.setMobileDistinctUsers(countUserByClientType("app"));
        clientLoginStats.setBothEndsUsers(countUserBoth());
        clientLoginStats.setPcLoginTimes(countByClientType("admin"));
        clientLoginStats.setMobileLoginTimes(countByClientType("app"));

        dashboardStatsVO.setClientLoginStats(clientLoginStats);

        return dashboardStatsVO;
    }

    @Override
    public DashBoardTrendVO getTrend(String period) {

        Map<String,Integer> periodDays = new HashMap<>();
        periodDays.put("week",7);
        periodDays.put("month",30);
        periodDays.put("year",365);


        int days = periodDays.get(period);
        // 左闭右开【start,end)
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        LocalDateTime start = end.minusDays(days);

        /*
        *   select date(pay_time) as dt,count(0) cnt, COALESCE(sum(pay_amount),0) amt  from ord_order
            where status in(1,2,3)
            and pay_time is not null
            and pay_time>='2008-08-08'
            and pay_time<='2038-08-08'
            GROUP BY   date(pay_time)
            order by dt asc
        */

        // 日期对应一行数据
        Map<String,Map<String,Object>> byDate = new HashMap<>();

        // 把表变成List<Map>
        List<Map<String, Object>> rows = selectDailyStats(start, end);
        for (Map<String, Object> row :rows){
            Object dtObj = row.get("dt");
            byDate.put(dtObj.toString(),row);
        }


        List<String> dates = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();
        List<Double> saleAmounts = new ArrayList<>();

        for(int i=0;i<days;i++){
            LocalDate d = LocalDate.now().minusDays(days - 1L - i);
            dates.add(d.format(DateTimeFormatter.ofPattern("MM-dd")));
            Map<String, Object> row = byDate.get(d.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if(CollectionUtil.isNotEmpty(row)) {
                orderCounts.add(Long.parseLong(row.get("cnt").toString()));
                saleAmounts.add(Double.parseDouble(row.get("amt").toString()));
            }else{
                orderCounts.add(0L);
                saleAmounts.add(0.0);
            }
        }

        DashBoardTrendVO trendVO = new DashBoardTrendVO();
        trendVO.setDates(dates);
        trendVO.setOrderCounts(orderCounts);
        trendVO.setSalesAmounts(saleAmounts);
        return trendVO;
    }

    @Override
    public List<DashBoardDistributionVO> getDistribution() {

        String[] colors = {"#3B82F6", "#10B981", "#F59E0B", "#8B5CF6", "#EF4444", "#6B7280", "#EC4899"};

        // 0待支付 1已支付 2已发货 3已完成 4已取消 5退款中 6已退款
        Map<String,String> statusNames = Map.of(
                "0","待支付",
                "1", "已支付",
                "2", "已发货",
                "3", "已完成",
                "4", "已取消",
                "5", "退款中",
                "6", "已退款"
        );

        /*select status name ,count(0) value from ord_order group by status*/
        QueryWrapper<OrdOrder> wrapper = new QueryWrapper<>();
        wrapper.select("status name ,count(0) value").groupBy("status");
        List<Map<String, Object>> rows = ordOrderMapper.selectMaps(wrapper);

        List<DashBoardDistributionVO> distributionVOList = new ArrayList<>();
        for(int i=0;i<rows.size();i++){
            Map<String, Object> row = rows.get(i);
            String name = statusNames.get(row.get("name").toString());
            String value = row.get("value").toString();
            DashBoardDistributionVO distributionVO = new DashBoardDistributionVO();
            distributionVO.setName(name);
            distributionVO.setValue(Integer.parseInt(value));
            DashBoardDistributionVO.ItemStyle itemStyle = new DashBoardDistributionVO.ItemStyle();
            itemStyle.setColor(colors[i]);
            distributionVO.setItemStyle(itemStyle);
            distributionVOList.add(distributionVO);
        }

        return distributionVOList;
    }

    @Override
    public List<DashBoardActivitiesVO> getActivities() {
        List<DashBoardActivitiesVO> activitiesVOList = new ArrayList<>();

        // 最新订单
        LambdaQueryWrapper<OrdOrder> ordOrderWrapper = new LambdaQueryWrapper<>();
        ordOrderWrapper.orderByDesc(OrdOrder::getCreateTime).last("limit 5");
        List<OrdOrder> ordOrderList = ordOrderMapper.selectList(ordOrderWrapper);
        for(OrdOrder ordOrder:ordOrderList){
            DashBoardActivitiesVO activitiesVO = new DashBoardActivitiesVO();
            activitiesVO.setContent("新订单 "+ordOrder.getOrderNo()+"￥"+ordOrder.getPayAmount()+" 完成支付");
            activitiesVO.setTime(timeAgo(ordOrder.getCreateTime()));
            activitiesVO.setColor("#F59E0B");
            activitiesVO.setTs(LocalDateTime.now());
            activitiesVOList.add(activitiesVO);
        }

        // 最新用户
        LambdaQueryWrapper<UsrCustomer> customerWrapper = new LambdaQueryWrapper<>();
        customerWrapper.orderByDesc(UsrCustomer::getCreateTime).last("limit 5");
        List<UsrCustomer> usrCustomerList = usrCustomerMapper.selectList(customerWrapper);


        for(UsrCustomer usrCustomer:usrCustomerList){
            DashBoardActivitiesVO activitiesVO = new DashBoardActivitiesVO();
            activitiesVO.setContent("新用户 "+usrCustomer.getNickName()+" 完成注册");
            activitiesVO.setTime(timeAgo(usrCustomer.getCreateTime()));
            activitiesVO.setColor("#10B981");
            activitiesVO.setTs(LocalDateTime.now());
            activitiesVOList.add(activitiesVO);
        }

        activitiesVOList.sort((o1, o2) -> o2.getTs().compareTo(o1.getTs()));

        return activitiesVOList;
    }

    private String timeAgo(LocalDateTime createTime) {

        LocalDateTime now = LocalDateTime.now();
        long mins = ChronoUnit.MINUTES.between(createTime, now);
        if(mins<60) {
            return mins+"分钟前";
        }

        long hours = ChronoUnit.HOURS.between(createTime, now);
        if(hours<24){
            return hours+"小时前";
        }
        long days = ChronoUnit.DAYS.between(createTime, now);
        return days+"天前";
    }

    private List<Map<String, Object>> selectDailyStats(LocalDateTime start,LocalDateTime end) {
        QueryWrapper<OrdOrder> wrapper = new QueryWrapper<>();
        wrapper.select("date(pay_time) as dt,count(0) cnt, COALESCE(sum(pay_amount),0) amt ");
        wrapper.in("status",PAID_STATUSES).isNotNull("pay_time");
        wrapper.ge("pay_time", start).le("pay_time", end);
        wrapper.groupBy("date(pay_time)").orderByAsc("dt");

        List<Map<String, Object>> maps = ordOrderMapper.selectMaps(wrapper);
        return maps;
    }
}
