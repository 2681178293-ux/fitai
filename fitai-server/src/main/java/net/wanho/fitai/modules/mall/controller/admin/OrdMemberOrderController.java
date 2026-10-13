package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.OrdMemberOrderAddDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdMemberOrderQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.OrdMemberOrderUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.OrdMemberOrderVO;
import net.wanho.fitai.modules.mall.service.OrdMemberOrderServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrdMemberOrderController")
@RequestMapping("/admin/order/member-order")
@RequiredArgsConstructor
@Tag(name = "会员订单接口")
public class OrdMemberOrderController {

    private final OrdMemberOrderServiceI ordmemberorderService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<OrdMemberOrderVO>> findByPage(OrdMemberOrderQueryDTO queryDto) {
        return AjaxResult.success(ordmemberorderService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<OrdMemberOrderVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(ordmemberorderService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加会员订单")
    public AjaxResult<Void> add(@Valid @RequestBody OrdMemberOrderAddDTO addDto) {
        ordmemberorderService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改会员订单")
    public AjaxResult<Void> update(@Valid @RequestBody OrdMemberOrderUpdateDTO updateDto) {
        ordmemberorderService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除会员订单")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        ordmemberorderService.delete(id);
        return AjaxResult.success();
    }

}
