package net.wanho.fitai.modules.media.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MediaOssDeleteDTO {

    @NotNull(message = "ossIds不能为空")
    private List<Long> ossIds;
}
