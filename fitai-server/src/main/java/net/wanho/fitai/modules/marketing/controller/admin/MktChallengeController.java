package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.MktChallengeVO;
import net.wanho.fitai.modules.marketing.service.MktChallengeServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMktChallengeController")
@RequestMapping("/admin/challenge/challenge")
@RequiredArgsConstructor
@Tag(name = "挑战赛接口")
public class MktChallengeController {

    private final MktChallengeServiceI mktchallengeService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MktChallengeVO>> findByPage(MktChallengeQueryDTO queryDto) {
        return AjaxResult.success(mktchallengeService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MktChallengeVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mktchallengeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加挑战赛")
    public AjaxResult<Void> add(@Valid @RequestBody MktChallengeAddDTO addDto) {
        mktchallengeService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改挑战赛")
    public AjaxResult<Void> update(@Valid @RequestBody MktChallengeUpdateDTO updateDto) {
        mktchallengeService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除挑战赛")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mktchallengeService.delete(id);
        return AjaxResult.success();
    }

}
