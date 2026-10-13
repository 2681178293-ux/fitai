package net.wanho.fitai.modules.dashbaord.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.dashbaord.model.vo.DashBoardActivitiesVO;
import net.wanho.fitai.modules.dashbaord.model.vo.DashBoardDistributionVO;
import net.wanho.fitai.modules.dashbaord.model.vo.DashBoardTrendVO;
import net.wanho.fitai.modules.dashbaord.model.vo.DashboardStatsVO;
import net.wanho.fitai.modules.dashbaord.service.AdminDashboardServiceI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@Tag(name ="仪表盘")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardServiceI adminDashboardService;

    @GetMapping("/stats")
    @Operation(summary = "核心数据统计")
    public AjaxResult<DashboardStatsVO> getStats(){
        return AjaxResult.success(adminDashboardService.getStats());
    }

    @GetMapping("/trend")
    @Operation(summary = "趋势数据")
    public AjaxResult<DashBoardTrendVO> getTrend(@RequestParam("period") String period) {
        return AjaxResult.success(adminDashboardService.getTrend(period));
    }

    @GetMapping("/activities")
    @Operation(summary = "最新活动")
    public AjaxResult<List<DashBoardActivitiesVO>> getActivities(){
        return AjaxResult.success(adminDashboardService.getActivities());
    }

    @GetMapping("/distribution")
    @Operation(summary = "订单状态分布")
    public AjaxResult<List<DashBoardDistributionVO>> getDistribution(){
        return AjaxResult.success(adminDashboardService.getDistribution());
    }


}
