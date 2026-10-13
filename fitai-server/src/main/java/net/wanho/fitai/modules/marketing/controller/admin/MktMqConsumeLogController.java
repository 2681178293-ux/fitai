package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.MktMqConsumeLogAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktMqConsumeLogQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktMqConsumeLogUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.MktMqConsumeLogVO;
import net.wanho.fitai.modules.marketing.service.MktMqConsumeLogServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMktMqConsumeLogController")
@RequestMapping("/admin/marketing/mq-consume-log")
@RequiredArgsConstructor
@Tag(name = "MQ消费幂等日志接口")
public class MktMqConsumeLogController {

    private final MktMqConsumeLogServiceI mktmqconsumelogService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MktMqConsumeLogVO>> findByPage(MktMqConsumeLogQueryDTO queryDto) {
        return AjaxResult.success(mktmqconsumelogService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MktMqConsumeLogVO> findById(@PathVariable("id") String id) {
        return AjaxResult.success(mktmqconsumelogService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加MQ消费幂等日志")
    public AjaxResult<Void> add(@Valid @RequestBody MktMqConsumeLogAddDTO addDto) {
        mktmqconsumelogService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改MQ消费幂等日志")
    public AjaxResult<Void> update(@Valid @RequestBody MktMqConsumeLogUpdateDTO updateDto) {
        mktmqconsumelogService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除MQ消费幂等日志")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mktmqconsumelogService.delete(id);
        return AjaxResult.success();
    }

}
