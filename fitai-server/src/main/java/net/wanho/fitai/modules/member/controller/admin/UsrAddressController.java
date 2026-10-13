package net.wanho.fitai.modules.member.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.member.model.dto.UsrAddressAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrAddressQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrAddressUpdateDTO;
import net.wanho.fitai.modules.member.model.vo.UsrAddressVO;
import net.wanho.fitai.modules.member.service.UsrAddressServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminUsrAddressController")
@RequestMapping("/admin/user/address")
@RequiredArgsConstructor
@Tag(name = "用户收货地址接口")
public class UsrAddressController {

    private final UsrAddressServiceI usraddressService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<UsrAddressVO>> findByPage(UsrAddressQueryDTO queryDto) {
        return AjaxResult.success(usraddressService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<UsrAddressVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(usraddressService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加用户收货地址")
    public AjaxResult<Void> add(@Valid @RequestBody UsrAddressAddDTO addDto) {
        usraddressService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改用户收货地址")
    public AjaxResult<Void> update(@Valid @RequestBody UsrAddressUpdateDTO updateDto) {
        usraddressService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除用户收货地址")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        usraddressService.delete(id);
        return AjaxResult.success();
    }

}
