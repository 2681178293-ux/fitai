package net.wanho.fitai.modules.community.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.wanho.fitai.modules.community.model.po.CommunityComment;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "社区评论表视图")
@AutoMapper(target = CommunityComment.class)
public class CommunityCommentVO implements Serializable {

    /**
     * 评论ID（雪花算法生成，数值可能超过 JS Number 安全整数范围）。
     * 序列化为字符串，避免前端 JSON 解析时精度丢失，导致置顶/删除等操作打到错误 ID。
     */
    @Schema(description = "评论ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @Schema(description = "动态ID")
    private Long postId;
    @Schema(description = "评论用户ID")
    private Long userId;
    @Schema(description = "父评论ID")
    private Long parentId;
    @Schema(description = "回复目标用户ID")
    private Long replyUserId;
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "状态：0-已删除 1-正常")
    private Integer status;
    @Schema(description = "是否置顶：0-否 1-是")
    private Integer top;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String nickname;
}
