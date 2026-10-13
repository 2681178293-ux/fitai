package net.wanho.fitai.modules.media.model.dto;

import lombok.Data;

@Data
public class MediaOssCategoryMoveDTO {

    private Long categoryId;
    private String direction;
    private Long targetCategoryId;
}
