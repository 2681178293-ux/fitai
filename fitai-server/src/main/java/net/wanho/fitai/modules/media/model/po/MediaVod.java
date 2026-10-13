package net.wanho.fitai.modules.media.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MediaVod implements Serializable {

    @TableId
    private Long vodAssetId;
    private String vodName;
    private Long categoryId;
    private String categoryName;
    private String bizModule;
    private Long bizId;
    private String vodVideoId;
    private String vodCoverUrl;
    private BigDecimal vodDuration;
    private Long vodSize;
    private String vodStatus;
    private Long vodCateId;
    private String vodStorageLocation;
    private String vodSnapshotUrls;
    private String status;
    private String delFlag;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
