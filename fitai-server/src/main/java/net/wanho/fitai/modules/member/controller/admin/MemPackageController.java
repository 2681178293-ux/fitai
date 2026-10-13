package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.MemPackageAddDTO;
import net.wanho.fitai.modules.member.model.dto.MemPackageQueryDTO;
import net.wanho.fitai.modules.member.model.dto.MemPackageUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.MemPackageVO;
import net.wanho.fitai.modules.member.service.MemPackageServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMemPackageController")
@RequestMapping("/admin/member/package")
@RequiredArgsConstructor
@Tag(name = "会员套餐接口")
public class MemPackageController {

    private final MemPackageServiceI mempackageService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MemPackageVO>> findByPage(MemPackageQueryDTO queryDto) {
        return AjaxResult.success(mempackageService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MemPackageVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mempackageService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加会员套餐")
    public AjaxResult<Void> add(@Valid @RequestBody MemPackageAddDTO addDto) {
        mempackageService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改会员套餐")
    public AjaxResult<Void> update(@Valid @RequestBody MemPackageUpdateDTO updateDto) {
        mempackageService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除会员套餐")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mempackageService.delete(id);
        return AjaxResult.success();
    }

}
