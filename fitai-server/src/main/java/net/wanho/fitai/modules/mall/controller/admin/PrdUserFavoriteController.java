package net.wanho.fitai.modules.mall.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.mall.model.dto.PrdUserFavoriteAddDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdUserFavoriteQueryDTO;
import net.wanho.fitai.modules.mall.model.dto.PrdUserFavoriteUpdateDTO;
import net.wanho.fitai.modules.mall.model.vo.PrdUserFavoriteVO;
import net.wanho.fitai.modules.mall.service.PrdUserFavoriteServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminPrdUserFavoriteController")
@RequestMapping("/admin/product/user-favorite")
@RequiredArgsConstructor
@Tag(name = "用户商品收藏接口")
public class PrdUserFavoriteController {

    private final PrdUserFavoriteServiceI prduserfavoriteService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<PrdUserFavoriteVO>> findByPage(PrdUserFavoriteQueryDTO queryDto) {
        return AjaxResult.success(prduserfavoriteService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<PrdUserFavoriteVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(prduserfavoriteService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户商品收藏")
    public AjaxResult<Void> add(@Valid @RequestBody PrdUserFavoriteAddDTO addDto) {
        prduserfavoriteService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户商品收藏")
    public AjaxResult<Void> update(@Valid @RequestBody PrdUserFavoriteUpdateDTO updateDto) {
        prduserfavoriteService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户商品收藏")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        prduserfavoriteService.delete(id);
        return AjaxResult.success();
    }

}
