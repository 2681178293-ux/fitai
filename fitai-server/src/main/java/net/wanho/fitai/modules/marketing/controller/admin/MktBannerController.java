package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.MktBannerAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktBannerQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktBannerUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.MktBannerVO;
import net.wanho.fitai.modules.marketing.service.MktBannerServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMktBannerController")
@RequestMapping("/admin/marketing/banner")
@RequiredArgsConstructor
@Tag(name = "轮播图接口")
public class MktBannerController {

    private final MktBannerServiceI mktbannerService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MktBannerVO>> findByPage(MktBannerQueryDTO queryDto) {
        return AjaxResult.success(mktbannerService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MktBannerVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mktbannerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加轮播图")
    public AjaxResult<Void> add(@Valid @RequestBody MktBannerAddDTO addDto) {
        mktbannerService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改轮播图")
    public AjaxResult<Void> update(@Valid @RequestBody MktBannerUpdateDTO updateDto) {
        mktbannerService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除轮播图")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mktbannerService.delete(id);
        return AjaxResult.success();
    }

}
