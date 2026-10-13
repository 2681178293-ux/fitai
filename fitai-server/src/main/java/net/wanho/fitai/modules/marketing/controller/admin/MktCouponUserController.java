package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUserAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUserQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktCouponUserUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.MktCouponUserVO;
import net.wanho.fitai.modules.marketing.service.MktCouponUserServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMktCouponUserController")
@RequestMapping("/admin/marketing/coupon-user")
@RequiredArgsConstructor
@Tag(name = "用户优惠券领取记录接口")
public class MktCouponUserController {

    private final MktCouponUserServiceI mktcouponuserService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MktCouponUserVO>> findByPage(MktCouponUserQueryDTO queryDto) {
        return AjaxResult.success(mktcouponuserService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MktCouponUserVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mktcouponuserService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户优惠券领取记录")
    public AjaxResult<Void> add(@Valid @RequestBody MktCouponUserAddDTO addDto) {
        mktcouponuserService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户优惠券领取记录")
    public AjaxResult<Void> update(@Valid @RequestBody MktCouponUserUpdateDTO updateDto) {
        mktcouponuserService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户优惠券领取记录")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mktcouponuserService.delete(id);
        return AjaxResult.success();
    }

}
