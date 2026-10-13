package net.wanho.fitai;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.vod20170321.Client;
import com.aliyun.vod20170321.models.*;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import net.wanho.fitai.infra.vod.config.VodProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class VodTest {

    @Resource
    private Client client;

    @Resource
    private VodProperties properties;

    /**
     * 点播上传分两步：
     * 1）调用 VOD 接口申请上传地址和临时凭证（不会传文件）
     * 2）用返回的临时凭证，通过 OSS SDK 真正把本地文件传到点播存储
     */
    @Test
    public void upload() throws Exception {
        // ---------- 第 1 步：向点播申请上传资格 ----------
        // Title：视频标题；CateId：分类；FileName：用于推断扩展名，点播据此分配存储对象名
        CreateUploadVideoRequest request = new CreateUploadVideoRequest()
                .setTitle("课程示例3")
                .setFileName("test.mp4")
                // 分类
                .setCateId(properties.getCateId())
                // 同时传 WorkflowId 和 TemplateGroupId 时，以 WorkflowId 为准
                //.setTemplateGroupId(TEMPLATE_GROUP_ID)
                .setWorkflowId(properties.getWorkflowId());

        // 仅创建媒资元数据并下发上传信息，此时控制台可能已有一条“上传中/未完成”的视频记录
        CreateUploadVideoResponse response = client.createUploadVideo(request);


        // UploadAuth / UploadAddress 都是 Base64 字符串，解码后才是 JSON
        // auth：临时 STS 凭证（AccessKeyId / AccessKeySecret / SecurityToken），有过期时间
        // addr：OSS 上传目标（Endpoint / Bucket / FileName）
        JSONObject auth = JSONUtil.parseObj(Base64.decodeStr(response.getBody().getUploadAuth()));
        JSONObject addr = JSONUtil.parseObj(Base64.decodeStr(response.getBody().getUploadAddress()));

        // ---------- 第 2 步：用临时凭证初始化 OSS，真正上传文件 ----------
        // 注意：必须用 UploadAuth 里的临时三件套，不能混用账号永久 AK + SecurityToken
        OSS oss = new OSSClientBuilder().build(
                addr.getStr("Endpoint"),
                auth.getStr("AccessKeyId"),
                auth.getStr("AccessKeySecret"),
                auth.getStr("SecurityToken"));
        try {
            // 同步上传：会阻塞到传完；耗时取决于文件大小和上行带宽
            // Bucket + FileName 必须和点播下发的一致，否则 VideoId 对不上
            oss.putObject(new PutObjectRequest(
                    addr.getStr("Bucket"),
                    addr.getStr("FileName"),
                    new File("D:\\WanHe_quanzhan\\step2\\素材\\video\\腹肌.mp4")));
        } finally {
            oss.shutdown();
        }

        // VideoId：后续查询、播放、转码都靠它
        System.out.println("Put local file succeed, VideoId : " + response.getBody().getVideoId());
    }

    // 获取播放地址
    @SneakyThrows
    @Test
    public void getPayUrl(){
        String videoId = "d0553373a4eb71f194a1e6e6d5480102";

        GetPlayInfoRequest request = new GetPlayInfoRequest().setVideoId(videoId);
        String playURL = client.getPlayInfo(request).getBody().getPlayInfoList().getPlayInfo().get(0).getPlayURL();
        System.out.println("Pay URL : " + playURL);
    }

    // 获取视频信息
    @SneakyThrows
    @Test
    public void getVideoInfo(){
        GetVideoInfoRequest request = new GetVideoInfoRequest().setVideoId("d0553373a4eb71f194a1e6e6d5480102");
        GetVideoInfoResponseBody.GetVideoInfoResponseBodyVideo video = client.getVideoInfo(request).getBody().getVideo();
        System.out.println(video.getTitle());
        System.out.println(video.getStatus());
        System.out.println(video.getDuration());
        System.out.println(video.getCoverURL());
        System.out.println(video.getCateId());
        System.out.println(video.getSize());
        System.out.println(video.getSnapshots().snapshot);
    }

    @SneakyThrows
    @Test
    public void deleteVideo() {
        DeleteVideoRequest request = new DeleteVideoRequest().setVideoIds("d0553373a4eb71f194a1e6e6d5480102");
        client.deleteVideo(request);
    }
}
