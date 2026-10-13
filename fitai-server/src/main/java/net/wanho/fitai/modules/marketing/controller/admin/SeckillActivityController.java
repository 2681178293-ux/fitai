package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.SeckillActivityAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillActivityQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.SeckillActivityUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.SeckillActivityVO;
import net.wanho.fitai.modules.marketing.service.SeckillActivityServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminSeckillActivityController")
@RequestMapping("/admin/seckill/activity")
@RequiredArgsConstructor
@Tag(name = "秒杀活动表接口")
public class SeckillActivityController {

    private final SeckillActivityServiceI seckillactivityService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<SeckillActivityVO>> findByPage(SeckillActivityQueryDTO queryDto) {
        return AjaxResult.success(seckillactivityService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<SeckillActivityVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(seckillactivityService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加秒杀活动表")
    public AjaxResult<Void> add(@Valid @RequestBody SeckillActivityAddDTO addDto) {
        seckillactivityService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改秒杀活动表")
    public AjaxResult<Void> update(@Valid @RequestBody SeckillActivityUpdateDTO updateDto) {
        seckillactivityService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除秒杀活动表")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        seckillactivityService.delete(id);
        return AjaxResult.success();
    }

}
