package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdMqConsumeLogAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdMqConsumeLogQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdMqConsumeLogUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdMqConsumeLogVO;
import net.wanho.fitai.modules.mall.service.PrdMqConsumeLogServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdMqConsumeLogController")
@RequestMapping("/admin/product/mq-consume-log")
@RequiredArgsConstructor
@Tag(name = "MQ消费幂等日志接口")
public class PrdMqConsumeLogController {

    private final PrdMqConsumeLogServiceI prdmqconsumelogService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdMqConsumeLogVO>> findByPage(PrdMqConsumeLogQueryDTO queryDto) {
        return AjaxResult.success(prdmqconsumelogService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdMqConsumeLogVO> findById(@PathVariable("id") String id) {
        return AjaxResult.success(prdmqconsumelogService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加MQ消费幂等日志")
    public AjaxResult<Void> add(@Valid @RequestBody PrdMqConsumeLogAddDTO addDto) {
        prdmqconsumelogService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改MQ消费幂等日志")
    public AjaxResult<Void> update(@Valid @RequestBody PrdMqConsumeLogUpdateDTO updateDto) {
        prdmqconsumelogService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除MQ消费幂等日志")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdmqconsumelogService.delete(id);
        return AjaxResult.success();
    }

}
