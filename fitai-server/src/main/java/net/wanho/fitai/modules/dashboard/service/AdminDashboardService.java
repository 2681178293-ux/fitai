package net.wanho.fitai.modules.dashboard.service;

import net.wanho.fitai.modules.dashboard.model.vo.DashBoardActivitiesVO;
import net.wanho.fitai.modules.dashboard.model.vo.DashBoardDistributionVO;
import net.wanho.fitai.modules.dashboard.model.vo.DashBoardTrendVO;
import net.wanho.fitai.modules.dashboard.model.vo.DashboardStatsVO;

import java.util.List;

public interface AdminDashboardService {
    DashboardStatsVO getStats();

    DashBoardTrendVO getTrend(String period);

    List<DashBoardDistributionVO> getDistribution();

    List<DashBoardActivitiesVO> getActivities();
}
