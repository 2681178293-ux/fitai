package net.wanho.fitai;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.models.*;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import net.wanho.fitai.infra.oss.config.OssProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


@SpringBootTest
class FitaiApplicationTests {

    @Resource
    private OSSClient ossClient;
    @Resource
    private OssProperties ossProperties;

    @Test
    void listObjects() {
        ListObjectsV2Result result = ossClient.listObjectsV2(ListObjectsV2Request.newBuilder()
                .bucket(ossProperties.getBucket())
                .prefix("08/08/")
                .build());
        result.contents().forEach(o -> System.out.println(o.key()));
    }

    @Test
    void createBucket() {
        boolean exists = ossClient.doesBucketExist("fitai-all11111");
        if (!exists) {
            ossClient.putBucket(PutBucketRequest.newBuilder()
                    .bucket("fitai-all11111")
                    .build());
        }
    }

    //删除桶
    @Test
    void deleteBucket() {
        ossClient.deleteBucket(DeleteBucketRequest.newBuilder()
                .bucket("fitai-all11111")
                .build());
    }

    @Test
    void delete(){
        DeleteObjectRequest req = DeleteObjectRequest.newBuilder()
                .bucket(ossProperties.getBucket())
                .key("08/08/a.jpg")
                .build();
        ossClient.deleteObject(req);
    }

    @Test
    void upload() throws Exception {
        File file = new File("D:/1.jpg");
        InputStream in = new FileInputStream(file);

        PutObjectRequest request = PutObjectRequest.newBuilder()
                .bucket(ossProperties.getBucket())
                .key("08/08/1.jpg")
                .body(BinaryData.fromStream(in))
                .contentType("image/jpeg")
                .build();
        ossClient.putObject(request);
        in.close();
    }

    @Resource
    private Converter converter;

    @Test
    void test1() {

    }

}
