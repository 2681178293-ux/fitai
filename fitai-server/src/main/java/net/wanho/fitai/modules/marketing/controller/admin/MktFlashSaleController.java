package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktFlashSaleUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.MktFlashSaleVO;
import net.wanho.fitai.modules.marketing.service.MktFlashSaleServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMktFlashSaleController")
@RequestMapping("/admin/marketing/flash-sale")
@RequiredArgsConstructor
@Tag(name = "秒杀活动接口")
public class MktFlashSaleController {

    private final MktFlashSaleServiceI mktflashsaleService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MktFlashSaleVO>> findByPage(MktFlashSaleQueryDTO queryDto) {
        return AjaxResult.success(mktflashsaleService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MktFlashSaleVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mktflashsaleService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加秒杀活动")
    public AjaxResult<Void> add(@Valid @RequestBody MktFlashSaleAddDTO addDto) {
        mktflashsaleService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改秒杀活动")
    public AjaxResult<Void> update(@Valid @RequestBody MktFlashSaleUpdateDTO updateDto) {
        mktflashsaleService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除秒杀活动")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mktflashsaleService.delete(id);
        return AjaxResult.success();
    }

}
