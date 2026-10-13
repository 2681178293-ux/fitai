package net.wanho.fitai.infra.vod.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aliyun.vod")
public class VodProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String regionId;
    private Long cateId;
    private String workflowId;
}
