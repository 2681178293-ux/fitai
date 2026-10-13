package net.wanho.fitai.modules.media.service.impl;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.vod20170321.Client;
import com.aliyun.vod20170321.models.CreateUploadVideoRequest;
import com.aliyun.vod20170321.models.CreateUploadVideoResponse;
import com.aliyun.vod20170321.models.DeleteVideoRequest;
import com.aliyun.vod20170321.models.GetPlayInfoRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.wanho.fitai.infra.exception.CustomException;
import net.wanho.fitai.infra.vod.config.VodProperties;
import net.wanho.fitai.modules.media.code.MediaCode;
import net.wanho.fitai.modules.media.mapper.MediaVodCategoryMapper;
import net.wanho.fitai.modules.media.mapper.MediaVodMapper;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.po.MediaVod;
import net.wanho.fitai.modules.media.model.po.MediaVodCategory;
import net.wanho.fitai.modules.media.model.vo.MediaVodVO;
import net.wanho.fitai.modules.media.service.MediaVodServiceI;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaVodServiceImpl extends MPJBaseServiceImpl<MediaVodMapper, MediaVod> implements MediaVodServiceI {

    private final Converter converter;
    private final Client client;
    private final VodProperties vodProperties;
    private final MediaVodCategoryMapper mediaVodCategoryMapper;

    @Override
    public IPage<MediaVodVO> findByPage(MediaVodQueryDTO queryDto) {

        IPage<MediaVod> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MediaVod> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getVodName()), MediaVod::getVodName, queryDto.getVodName());
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCategoryName()), MediaVod::getCategoryName, queryDto.getCategoryName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), MediaVod::getStatus, queryDto.getStatus());
        IPage<MediaVod> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MediaVodVO.class));

    }

    @Override
    public void add(MediaVodAddDTO addDto) {
        MediaVod mediavod = converter.convert(addDto, MediaVod.class);
        this.save(mediavod);
    }

    @Override
    public void update(MediaVodUpdateDTO updateDto) {
        MediaVod mediavod = converter.convert(updateDto, MediaVod.class);
        this.updateById(mediavod);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MediaVodVO findById(Long id) {
        MediaVod entity = this.getById(id);
        return converter.convert(entity, MediaVodVO.class);
    }

    @SneakyThrows
    @Override
    public MediaVodVO upload(MediaVodUploadDTO uploadDto) {

        // 获取上传的文件
        MultipartFile file = uploadDto.getFile();

        // 获取原始名称
        String originalFilename = file.getOriginalFilename();
        // 获取扩展名
        String extName = FileUtil.extName(originalFilename);
        // 生成新的文件名
        String fileName = IdUtil.fastSimpleUUID() + "." + extName;

        // 上传到阿里的标题，我们用文件名作为标题
        String title = FileUtil.mainName(originalFilename);

        // upload
        CreateUploadVideoRequest request = new CreateUploadVideoRequest()
                .setTitle(title)
                .setFileName(fileName)
                // 分类
                .setCateId(vodProperties.getCateId())
                // 工作流
                .setWorkflowId(vodProperties.getWorkflowId());

        // 仅创建媒资元数据并下发上传信息，此时控制台可能已有一条“上传中/未完成”的视频记录
        CreateUploadVideoResponse response = client.createUploadVideo(request);

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
                    uploadDto.getFile().getInputStream()
                    ));
        } finally {
            oss.shutdown();
        }

        // VideoId：后续查询、播放、转码都靠它
        String videoId = response.getBody().getVideoId();


        Long categoryId = uploadDto.getCategoryId();
        MediaVodCategory mediaVodCategory = mediaVodCategoryMapper.selectById(categoryId);

        // saveDB
        MediaVod mediaVod = new MediaVod();
        mediaVod.setVodName(title);
        mediaVod.setCategoryId(categoryId);
        mediaVod.setCategoryName(mediaVodCategory.getCategoryName());
        mediaVod.setBizModule("library");
        mediaVod.setVodVideoId(videoId);
        mediaVod.setVodCateId(vodProperties.getCateId());
        mediaVod.setStatus("0");
        this.save(mediaVod);

        return converter.convert(mediaVod, MediaVodVO.class);
    }

    @Override
    public IPage<MediaVodVO>  findByPage(MediaVodQuerySimpleDTO queryDto) {

        List<Long> ids = mediaVodCategoryMapper.selectList(
                        new LambdaQueryWrapper<MediaVodCategory>()
                                .eq(MediaVodCategory::getCategoryId, queryDto.getCategoryId())
                                .or()
                                .apply("find_in_set({0},ancestors)", queryDto.getCategoryId())
                ).stream()
                .map(MediaVodCategory::getCategoryId).toList();


        IPage<MediaVod> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MediaVod> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CollUtil.isNotEmpty(ids), MediaVod::getCategoryId, ids);
        wrapper.like(StrUtil.isNotEmpty(queryDto.getKeyword()), MediaVod::getVodName, queryDto.getKeyword());
        return this.page(page, wrapper).convert(item -> converter.convert(item, MediaVodVO.class));
    }

    @SneakyThrows
    @Override
    public String getPlayUrl(Long id) {
        MediaVod entity = this.getById(id);
        if (entity == null || StrUtil.isBlank(entity.getVodVideoId())) {
            throw new CustomException(MediaCode.MEDIA_VOD_NOT_FOUND);
        }
        GetPlayInfoRequest request = new GetPlayInfoRequest()
                .setVideoId(entity.getVodVideoId())
                .setAuthTimeout(3600L);
        var playInfoList = client.getPlayInfo(request).getBody().getPlayInfoList();
        if (playInfoList == null || playInfoList.getPlayInfo() == null || playInfoList.getPlayInfo().isEmpty()) {
            throw new CustomException(MediaCode.VOD_PLAY_URL_NOT_READY);
        }
        return playInfoList.getPlayInfo().get(0).getPlayURL();
    }

    @SneakyThrows
    @Override
    public void delete(MediaVodDeleteDTO deleteDTO) {
        List<Long> vodIds = deleteDTO.getVodAssetIds();
        // remove-oss
        List<MediaVod> mediaVods = this.listByIds(vodIds);
        for(MediaVod mediaVod : mediaVods){
            String videoId = mediaVod.getVodVideoId();

            DeleteVideoRequest request = new DeleteVideoRequest().setVideoIds(videoId);
            client.deleteVideo(request);
        }

        // remove-db
        this.removeByIds(vodIds);
    }
}
