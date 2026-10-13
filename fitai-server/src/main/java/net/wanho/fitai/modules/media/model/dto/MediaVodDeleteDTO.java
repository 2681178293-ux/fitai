package net.wanho.fitai.modules.media.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MediaVodDeleteDTO {

    @NotNull(message = "vodIds不能为空")
    private List<Long> vodAssetIds;
}
