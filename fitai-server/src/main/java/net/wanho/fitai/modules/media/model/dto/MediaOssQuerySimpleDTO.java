package net.wanho.fitai.modules.media.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.wanho.fitai.infra.page.PageQuery;

@Data
public class MediaOssQuerySimpleDTO extends PageQuery {

    @NotNull(message = "媒体分类不能为空")
    private Long categoryId;

    private String keyword;

}
