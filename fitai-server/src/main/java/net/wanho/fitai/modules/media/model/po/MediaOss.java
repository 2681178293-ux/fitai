package net.wanho.fitai.modules.media.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MediaOss implements Serializable {

    @TableId
    private Long ossId;
    private String ossName;
    private Long categoryId;
    private String categoryName;
    private String bizModule;
    private Long bizId;
    private String ossBucket;
    private String ossObjectKey;
    private String ossUrl;
    private String ossFileName;
    private Long ossFileSize;
    private String ossEtag;
    private String mimeType;
    private Integer width;
    private Integer height;
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
