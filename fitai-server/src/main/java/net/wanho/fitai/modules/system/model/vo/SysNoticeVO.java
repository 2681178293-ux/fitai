package net.wanho.fitai.modules.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.system.model.po.SysNotice;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "SysNotice视图")
@AutoMapper(target = SysNotice.class)
public class SysNoticeVO implements Serializable {
    @Schema(description = "公告ID")
    private Long noticeId;
    @Schema(description = "公告标题")
    private String noticeTitle;
    @Schema(description = "公告类型")
    private String noticeType;
    @Schema(description = "公告内容")
    private String noticeContent;
    @Schema(description = "目标类型")
    private String targetType;
    @Schema(description = "目标ID列表")
    private String targetIds;
    @Schema(description = "推送渠道")
    private String pushChannel;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;
    @Schema(description = "创建人")
    private String createBy;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}
