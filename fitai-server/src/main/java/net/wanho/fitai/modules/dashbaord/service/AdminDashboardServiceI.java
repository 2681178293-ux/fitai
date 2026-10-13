package net.wanho.fitai.modules.dashbaord.service;

import net.wanho.fitai.modules.dashbaord.model.vo.DashBoardActivitiesVO;
import net.wanho.fitai.modules.dashbaord.model.vo.DashBoardDistributionVO;
import net.wanho.fitai.modules.dashbaord.model.vo.DashBoardTrendVO;
import net.wanho.fitai.modules.dashbaord.model.vo.DashboardStatsVO;

import java.util.List;

public interface AdminDashboardServiceI {
    DashboardStatsVO getStats();

    DashBoardTrendVO getTrend(String period);

    List<DashBoardDistributionVO> getDistribution();

    List<DashBoardActivitiesVO> getActivities();
}
