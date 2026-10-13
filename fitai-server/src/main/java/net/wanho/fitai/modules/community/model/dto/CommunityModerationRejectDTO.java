package net.wanho.fitai.modules.community.model.dto;

import lombok.Data;

@Data
public class CommunityModerationRejectDTO {

    private Long moderationId;
    private String reason;
}
