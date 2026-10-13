package net.wanho.fitai.modules.marketing.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.system.log.OperLog;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeRecordAddDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeRecordQueryDTO;
import net.wanho.fitai.modules.marketing.model.dto.MktChallengeRecordUpdateDTO;
import net.wanho.fitai.modules.marketing.model.vo.MktChallengeRecordVO;
import net.wanho.fitai.modules.marketing.service.MktChallengeRecordServiceI;
import org.springframework.web.bind.annotation.*;

@RestController("AdminMktChallengeRecordController")
@RequestMapping("/admin/challenge/challenge-record")
@RequiredArgsConstructor
@Tag(name = "挑战赛参与记录接口")
public class MktChallengeRecordController {

    private final MktChallengeRecordServiceI mktchallengerecordService;

    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public AjaxResult<IPage<MktChallengeRecordVO>> findByPage(MktChallengeRecordQueryDTO queryDto) {
        return AjaxResult.success(mktchallengerecordService.findByPage(queryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询")
    public AjaxResult<MktChallengeRecordVO> findById(@PathVariable("id") Long id) {
        return AjaxResult.success(mktchallengerecordService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加")
    @OperLog(title = "添加挑战赛参与记录")
    public AjaxResult<Void> add(@Valid @RequestBody MktChallengeRecordAddDTO addDto) {
        mktchallengerecordService.add(addDto);
        return AjaxResult.success();
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperLog(title = "修改挑战赛参与记录")
    public AjaxResult<Void> update(@Valid @RequestBody MktChallengeRecordUpdateDTO updateDto) {
        mktchallengerecordService.update(updateDto);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @OperLog(title = "删除挑战赛参与记录")
    public AjaxResult<Void> delete(@PathVariable("id") String id) {
        mktchallengerecordService.delete(id);
        return AjaxResult.success();
    }

}
