package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.member.model.dto.UsrBodyMetricAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrBodyMetricQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrBodyMetricUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.UsrBodyMetricKpiVO;
import net.wanho.fitai.modules.member.model.vo.UsrBodyMetricVO;
import net.wanho.fitai.modules.member.service.UsrBodyMetricServiceI;
import net.wanho.fitai.modules.system.log.OperLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminUsrBodyMetricController")
@RequestMapping("/admin/user/body")
@RequiredArgsConstructor
@Tag(name = "体测数据接口")
public class UsrBodyMetricController {

    private final UsrBodyMetricServiceI usrbodymetricService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrBodyMetricVO>> findByPage(UsrBodyMetricQueryDTO queryDto) {
        return AjaxResult.success(usrbodymetricService.findByPage(queryDto));
    }

    @GetMapping("/kpi")
    @Operation(summary = "体测数据 KPI")
    public AjaxResult<UsrBodyMetricKpiVO> getKpi() {
        return AjaxResult.success(usrbodymetricService.getKpi());
    }

    @GetMapping("/trend/{customerId}")
    @Operation(summary = "用户体测趋势")
    public AjaxResult<List<UsrBodyMetricVO>> findTrend(
            @PathVariable("customerId") Long customerId,
            @RequestParam(value = "limit", defaultValue = "30") Integer limit) {
        return AjaxResult.success(usrbodymetricService.findTrend(customerId, limit));
    }




    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrBodyMetricVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usrbodymetricService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加体测数据")
    public AjaxResult<Void> add(@Valid @RequestBody UsrBodyMetricAddDTO addDto) {
        usrbodymetricService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改体测数据")
    public AjaxResult<Void> update(@Valid @RequestBody UsrBodyMetricUpdateDTO updateDto) {
        usrbodymetricService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除体测数据")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usrbodymetricService.delete(id);
        return AjaxResult.success();
    }

}
