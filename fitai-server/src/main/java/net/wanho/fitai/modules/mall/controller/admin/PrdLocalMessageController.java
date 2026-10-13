package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdLocalMessageAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdLocalMessageQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdLocalMessageUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdLocalMessageVO;
import net.wanho.fitai.modules.mall.service.PrdLocalMessageServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdLocalMessageController")
@RequestMapping("/admin/product/local-message")
@RequiredArgsConstructor
@Tag(name = "本地消息表(商品ES同步可靠消息)接口")
public class PrdLocalMessageController {

    private final PrdLocalMessageServiceI prdlocalmessageService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdLocalMessageVO>> findByPage(PrdLocalMessageQueryDTO queryDto) {
        return AjaxResult.success(prdlocalmessageService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdLocalMessageVO> findById(@PathVariable("id") String id) {
        return AjaxResult.success(prdlocalmessageService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加本地消息表(商品ES同步可靠消息)")
    public AjaxResult<Void> add(@Valid @RequestBody PrdLocalMessageAddDTO addDto) {
        prdlocalmessageService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改本地消息表(商品ES同步可靠消息)")
    public AjaxResult<Void> update(@Valid @RequestBody PrdLocalMessageUpdateDTO updateDto) {
        prdlocalmessageService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除本地消息表(商品ES同步可靠消息)")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prdlocalmessageService.delete(id);
        return AjaxResult.success();
    }

}
