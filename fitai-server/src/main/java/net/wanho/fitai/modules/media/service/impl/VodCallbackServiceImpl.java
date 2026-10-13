package net.wanho.fitai.modules.media.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.aliyun.vod20170321.Client;
import com.aliyun.vod20170321.models.GetVideoInfoRequest;
import com.aliyun.vod20170321.models.GetVideoInfoResponseBody;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.wanho.fitai.infra.oss.config.OssProperties;
import net.wanho.fitai.modules.media.mapper.MediaVodMapper;
import net.wanho.fitai.modules.media.model.po.MediaVod;
import net.wanho.fitai.modules.media.service.VodCallbackServiceI;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 阿里云 VOD 回调服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VodCallbackServiceImpl implements VodCallbackServiceI {

    private final Client client;
    private final MediaVodMapper mediaVodMapper;
    private final OSSClient ossClient;
    private final OssProperties ossProperties;

    @Override
    public void handleCallback(Map<String, Object> payload) {
        String eventType = (String) payload.get("EventType");
        String aliyunVideoId = (String) payload.get("VideoId");
        String status = (String) payload.get("Status");
        String eventTime = (String) payload.get("EventTime");

        log.info("收到阿里云VOD回调，EventType={}, VideoId={}, Status={}, EventTime={}",
                eventType, aliyunVideoId, status, eventTime);

        try {
            switch (eventType) {
                case "FileUploadComplete" -> handleUploadComplete(aliyunVideoId, payload);
                case "StreamTranscodeComplete" -> handleStreamTranscodeComplete(aliyunVideoId, status, payload);
                case "SnapshotComplete" -> handleSnapshotComplete(aliyunVideoId, payload);
                case "WorkflowTaskComplete"-> handleWorkflowTaskComplete(aliyunVideoId, payload);
                default -> log.info("未处理的事件类型: {}", eventType);
            }
        } catch (Exception e) {
            log.error("处理VOD回调失败，EventType={}, VideoId={}", eventType, aliyunVideoId, e);
        }
    }

    @SneakyThrows
    private void handleWorkflowTaskComplete(String aliyunVideoId, Map<String, Object> payload) {

    }

    @SneakyThrows
    private void handleUploadComplete(String aliyunVideoId, Map<String, Object> payload) {


    }

    @SneakyThrows
    private void handleStreamTranscodeComplete(String aliyunVideoId, String status, Map<String, Object> payload) {
        GetVideoInfoRequest request = new GetVideoInfoRequest().setVideoId(aliyunVideoId);
        GetVideoInfoResponseBody.GetVideoInfoResponseBodyVideo video = client.getVideoInfo(request).getBody().getVideo();

        MediaVod mediaVod = mediaVodMapper.selectOne(new LambdaQueryWrapper<MediaVod>().eq(MediaVod::getVodVideoId, aliyunVideoId));
        if (ObjectUtil.isNull(mediaVod)) {
            return;
        }
        mediaVod.setVodDuration(new BigDecimal(video.getDuration()));
        mediaVod.setVodSize(video.getSize());
        mediaVod.setVodStatus(video.getStatus());
        //mediaVod.setVodStorageLocation(video.getStorageLocation());
        mediaVodMapper.updateById(mediaVod);

        // 截图回调可能晚于转码，封面仍为空时补转存
        transferCoverToOss(mediaVod, video.getCoverURL());
    }

    @SneakyThrows
    private void handleSnapshotComplete(String aliyunVideoId, Map<String, Object> payload) {
        MediaVod mediaVod = mediaVodMapper.selectOne(new LambdaQueryWrapper<MediaVod>().eq(MediaVod::getVodVideoId, aliyunVideoId));
        if (ObjectUtil.isNull(mediaVod)) {
            return;
        }
        String coverUrl = firstNonBlank(payload.get("CoverUrl"), payload.get("CoverURL"));
        if (StrUtil.isBlank(coverUrl)) {
            GetVideoInfoRequest request = new GetVideoInfoRequest().setVideoId(aliyunVideoId);
            coverUrl = client.getVideoInfo(request).getBody().getVideo().getCoverURL();
        }
        transferCoverToOss(mediaVod, coverUrl);
    }

    private void transferCoverToOss(MediaVod mediaVod, String coverUrl) {
        if (StrUtil.isBlank(coverUrl)) {
            return;
        }
        if (StrUtil.isNotBlank(mediaVod.getVodCoverUrl())
                && mediaVod.getVodCoverUrl().startsWith(ossProperties.getDomain())) {
            return;
        }
        byte[] bytes = HttpUtil.downloadBytes(coverUrl);
        if (bytes == null || bytes.length == 0) {
            log.warn("下载VOD封面失败，videoId={}", mediaVod.getVodVideoId());
            return;
        }
        String ext = FileUtil.extName(StrUtil.subBefore(coverUrl, "?", false));
        if (StrUtil.isBlank(ext)) {
            ext = "jpg";
        }
        String key = "media/vod/cover/"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                + "/"
                + IdUtil.fastSimpleUUID()
                + "."
                + ext;
        PutObjectRequest request = PutObjectRequest.newBuilder()
                .bucket(ossProperties.getBucket())
                .key(key)
                .body(BinaryData.fromStream(new ByteArrayInputStream(bytes)))
                .contentType("image/" + ("jpg".equalsIgnoreCase(ext) ? "jpeg" : ext))
                .build();
        ossClient.putObject(request);

        mediaVod.setVodCoverUrl(ossProperties.getDomain() + key);
        mediaVodMapper.updateById(mediaVod);
        log.info("VOD封面已转存OSS，videoId={}, url={}", mediaVod.getVodVideoId(), mediaVod.getVodCoverUrl());
    }

    private String firstNonBlank(Object... values) {
        for (Object value : values) {
            if (value instanceof String s && StrUtil.isNotBlank(s)) {
                return s;
            }
        }
        return null;
    }

}
