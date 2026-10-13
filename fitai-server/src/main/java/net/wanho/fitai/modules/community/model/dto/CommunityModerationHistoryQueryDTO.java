package net.wanho.fitai.modules.community.model.dto;

import lombok.Data;
import net.wanho.fitai.infra.page.PageQuery;

@Data
public class CommunityModerationHistoryQueryDTO extends PageQuery {

    private String targetType;
    private String auditStatus;
}
