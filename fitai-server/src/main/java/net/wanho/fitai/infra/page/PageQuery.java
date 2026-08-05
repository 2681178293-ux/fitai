package net.wanho.fitai.infra.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基类。业务 QueryDTO 继承本类，统一携带 pageNum / pageSize。
 */
@Data
@Schema(description = "分页查询参数")
public class PageQuery implements Serializable {

    @Schema(description = "当前页", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}
