package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.MktCouponVO;
import net.wanho.fitai.modules.marketing.service.MktCouponServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMktCouponController")
@RequestMapping("/admin/marketing/coupon")
@RequiredArgsConstructor
@Tag(name = "优惠券接口")
public class MktCouponController {

    private final MktCouponServiceI mktcouponService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MktCouponVO>> findByPage(MktCouponQueryDTO queryDto) {
        return AjaxResult.success(mktcouponService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MktCouponVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mktcouponService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加优惠券")
    public AjaxResult<Void> add(@Valid @RequestBody MktCouponAddDTO addDto) {
        mktcouponService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改优惠券")
    public AjaxResult<Void> update(@Valid @RequestBody MktCouponUpdateDTO updateDto) {
        mktcouponService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除优惠券")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mktcouponService.delete(id);
        return AjaxResult.success();
    }

}
