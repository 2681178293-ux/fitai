package net.wanho.fitai.modules.mall.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminProductVO{
    private Long spuId;
    private Long categoryId;
    private Long brandId;
    private String spuName;
    private String spuSn;
    private String subtitle;
    private String unit;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer totalStock;
    private Integer salesCount;
    private Integer orderNum;
    /** 0上架 1下架 */
    private String status;
    private LocalDateTime createTime;
    /** 联表：分类名 */
    private String categoryName;
    /** 联表：品牌名 */
    private String brandName;
    /** 主图 URL */
    private String coverUrl;
    /** 图册 */
    private List<String> images;
    /** 详情表 description */
    private String description;
    /** 详情表 detailHtml */
    private String detailHtml;
    /** 库存是否低于预警 */
    private Boolean stockWarning;
}
