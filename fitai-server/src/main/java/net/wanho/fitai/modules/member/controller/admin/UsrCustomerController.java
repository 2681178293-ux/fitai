package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.UsrCustomerAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrCustomerQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrCustomerUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.UsrCustomerVO;
import net.wanho.fitai.modules.member.service.UsrCustomerServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminUsrCustomerController")
@RequestMapping("/admin/user/customer")
@RequiredArgsConstructor
@Tag(name = "C端用户接口")
public class UsrCustomerController {

    private final UsrCustomerServiceI usrcustomerService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrCustomerVO>> findByPage(UsrCustomerQueryDTO queryDto) {
        return AjaxResult.success(usrcustomerService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrCustomerVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usrcustomerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加C端用户")
    public AjaxResult<Void> add(@Valid @RequestBody UsrCustomerAddDTO addDto) {
        usrcustomerService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改C端用户")
    public AjaxResult<Void> update(@Valid @RequestBody UsrCustomerUpdateDTO updateDto) {
        usrcustomerService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除C端用户")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usrcustomerService.delete(id);
        return AjaxResult.success();
    }

}
