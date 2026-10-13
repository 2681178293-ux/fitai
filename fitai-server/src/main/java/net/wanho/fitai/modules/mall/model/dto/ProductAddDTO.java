package net.wanho.fitai.modules.mall.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductAddDTO {

    private String spuId;
    private String spuName;
    private Long categoryId;
    private Long brandId;
    private String coverUrl;
    private String description;
    private String detailHtml;
    private List<String> images;
}
