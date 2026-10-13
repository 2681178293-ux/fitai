package net.wanho.fitai.modules.member.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wanho.fitai.infra.page.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "C端用户查询参数")
public class UsrCustomerQueryDTO extends PageQuery {

    @Schema(description = "昵称")
    private String nickName;
    @Schema(description = "手机号")
    private String mobile;
    @Schema(description = "状态（0正常 1冻结）")
    private String status;
}
