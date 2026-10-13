package net.wanho.fitai.modules.member.model.dto;


import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import net.wanho.fitai.modules.member.model.po.MemCustomerBadge;

@Data
@Schema(description = "用户徽章修改参数")
@AutoMapper(target = MemCustomerBadge.class)
public class MemCustomerBadgeUpdateDTO implements Serializable {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;
    @Schema(description = "用户ID")
    private Long customerId;
    @Schema(description = "徽章ID")
    private Long badgeId;
    @Schema(description = "解锁时间")
    private LocalDateTime unlockTime;
    @Schema(description = "是否展示 0否 1是")
    private String isDisplayed;
}
