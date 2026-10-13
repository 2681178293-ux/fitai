package net.wanho.fitai.infra.oss.config;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.Credentials;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssConfig {

    @Bean(destroyMethod = "close")
    public OSSClient ossClient(OssProperties props) {
        return OSSClient.newBuilder()
                .credentialsProvider(
                        () -> new Credentials(props.getAccessKeyId(), props.getAccessKeySecret()))
                .region(props.getRegion())
                .build();
    }
}
