package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemCustomerBadge;

@Data
@Schema(description = "新增用户徽章")
@AutoMapper(target = MemCustomerBadge.class)
public class MemCustomerBadgeAddDTO implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long customerId;
    @Schema(description = "徽章ID")
    @NotNull(message = "徽章ID不能为空")
    private Long badgeId;
    @Schema(description = "解锁时间")
    private LocalDateTime unlockTime;
    @Schema(description = "是否展示 0否 1是")
    private String isDisplayed;
}
