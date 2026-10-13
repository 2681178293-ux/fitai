package net.wanho.fitai.infra.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {

    private String region;
    private String bucket;
    private String accessKeyId;
    private String accessKeySecret;
    private String domain = "";
}
