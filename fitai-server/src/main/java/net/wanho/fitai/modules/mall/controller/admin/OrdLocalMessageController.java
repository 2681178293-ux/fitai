package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.OrdLocalMessageAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLocalMessageQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdLocalMessageUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.OrdLocalMessageVO;
import net.wanho.fitai.modules.mall.service.OrdLocalMessageServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrdLocalMessageController")
@RequestMapping("/admin/order/local-message")
@RequiredArgsConstructor
@Tag(name = "本地消息表(可靠消息最终一致性)接口")
public class OrdLocalMessageController {

    private final OrdLocalMessageServiceI ordlocalmessageService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<OrdLocalMessageVO>> findByPage(OrdLocalMessageQueryDTO queryDto) {
        return AjaxResult.success(ordlocalmessageService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<OrdLocalMessageVO> findById(@PathVariable("id") String id) {
        return AjaxResult.success(ordlocalmessageService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加本地消息表(可靠消息最终一致性)")
    public AjaxResult<Void> add(@Valid @RequestBody OrdLocalMessageAddDTO addDto) {
        ordlocalmessageService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改本地消息表(可靠消息最终一致性)")
    public AjaxResult<Void> update(@Valid @RequestBody OrdLocalMessageUpdateDTO updateDto) {
        ordlocalmessageService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除本地消息表(可靠消息最终一致性)")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        ordlocalmessageService.delete(id);
        return AjaxResult.success();
    }

}
