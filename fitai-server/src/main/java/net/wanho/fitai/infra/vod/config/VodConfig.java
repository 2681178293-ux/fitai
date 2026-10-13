package net.wanho.fitai.infra.vod.config;

import com.aliyun.teaopenapi.models.Config;
import com.aliyun.vod20170321.Client;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(VodProperties.class)
public class VodConfig {

    @SneakyThrows
    @Bean
    public Client voidClient(VodProperties props) {
        Config config = new Config();
        config.setAccessKeyId(props.getAccessKeyId());
        config.setAccessKeySecret(props.getAccessKeySecret());
        config.setRegionId(props.getRegionId()); // Set the region where the VOD service is located
        config.setEndpoint("vod."+props.getRegionId()+".aliyuncs.com"); // Set the endpoint of the VOD service
        Client client = new Client(config);
        return client;
    }
}
