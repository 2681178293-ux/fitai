package net.wanho.fitai.modules.media.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.models.DeleteObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.wanho.fitai.infra.oss.config.OssProperties;
import net.wanho.fitai.modules.media.mapper.MediaOssCategoryMapper;
import net.wanho.fitai.modules.media.mapper.MediaOssMapper;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.po.MediaOss;
import net.wanho.fitai.modules.media.model.po.MediaOssCategory;
import net.wanho.fitai.modules.media.model.vo.MediaOssVO;
import net.wanho.fitai.modules.media.service.MediaOssServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaOssServiceImpl extends MPJBaseServiceImpl<MediaOssMapper, MediaOss> implements MediaOssServiceI {

    private final Converter converter;
    private final OSSClient ossClient;
    private final OssProperties ossProperties;
    private final MediaOssCategoryMapper categoryMapper;

    @Override
    public IPage<MediaOssVO> findByPage(MediaOssQueryDTO queryDto) {

        List<Long> ids = categoryMapper.selectList(
                        new LambdaQueryWrapper<MediaOssCategory>()
                                .eq(MediaOssCategory::getCategoryId, queryDto.getCategoryId())
                                .or()
                                .apply("find_in_set({0},ancestors)", queryDto.getCategoryId())
                ).stream()
                .map(MediaOssCategory::getCategoryId).toList();


        IPage<MediaOss> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MediaOss> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getKeyword()), MediaOss::getCategoryName, queryDto.getKeyword());
        wrapper.in(CollUtil.isNotEmpty(ids), MediaOss::getCategoryId, ids);
        IPage<MediaOss> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MediaOssVO.class));

    }

    @Override
    public void add(MediaOssAddDTO addDto) {
        MediaOss mediaoss = converter.convert(addDto, MediaOss.class);
        this.save(mediaoss);
    }

    @Override
    public void update(MediaOssUpdateDTO updateDto) {
        MediaOss mediaoss = converter.convert(updateDto, MediaOss.class);
        this.updateById(mediaoss);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MediaOssVO findById(Long id) {
        MediaOss entity = this.getById(id);
        return converter.convert(entity, MediaOssVO.class);
    }

    @SneakyThrows    // 抛出异常
    @Override
    public MediaOssVO upload(MediaOssUploadDTO uploadDto) {

        MultipartFile file = uploadDto.getFile();
        Long categoryId = uploadDto.getCategoryId();
        MediaOssCategory mediaOssCategory = categoryMapper.selectById(categoryId);

        //上传文件不是“图片对象”，是字节流
        InputStream inputStream = file.getInputStream();
        BufferedImage image = ImageIO.read(inputStream);     //读流
        inputStream.close();
        int width = image.getWidth();
        int height = image.getHeight();

        //获取文件的拓展名
        String originalFilename = file.getOriginalFilename();  //浏览器带来的原始文件名
        String extName = FileUtil.extName(originalFilename);

        String folder = "media/oss/";   //Bucket 内固定前缀，素材库文件都放这棵目录下。
        String dayPath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";
        String fileName = IdUtil.fastSimpleUUID() + "." + extName;      //用UUID当对象名
        String key = folder + dayPath + fileName;   ///例如 media/oss/2026/09/15/a1b2c3.png。Bucket 内唯一路径。

        //upload
        PutObjectRequest request = PutObjectRequest.newBuilder()
                .bucket(ossProperties.getBucket())
                .key(key)
                .body(BinaryData.fromStream( file.getInputStream()))
                .build();
        ossClient.putObject(request);       //真正上传


        // savedb
        MediaOss mediaOss = new MediaOss();
        mediaOss.setOssName(FileUtil.mainName(originalFilename));
        mediaOss.setCategoryId(categoryId);
        mediaOss.setCategoryName(mediaOssCategory.getCategoryName());
        mediaOss.setBizModule("library");
        mediaOss.setOssBucket(ossProperties.getBucket());
        mediaOss.setOssObjectKey(key);
        mediaOss.setOssUrl(ossProperties.getDomain() + key);
        mediaOss.setOssFileName(originalFilename);
        mediaOss.setOssFileSize(file.getSize());
        mediaOss.setMimeType(file.getContentType());
        mediaOss.setWidth(width);
        mediaOss.setHeight(height);
        mediaOss.setStatus("0");

        this.save(mediaOss);
        return converter.convert(mediaOss, MediaOssVO.class);
    }

    @Override
    public IPage<MediaOssVO> findByPage(MediaOssQuerySimpleDTO queryDto) {
        List<Long> ids = categoryMapper.selectList(
                        new LambdaQueryWrapper<MediaOssCategory>()
                                .eq(MediaOssCategory::getCategoryId, queryDto.getCategoryId())
                                .or()
                                .apply("find_in_set({0},ancestors)", queryDto.getCategoryId())
                ).stream()
                .map(MediaOssCategory::getCategoryId).toList();

        IPage<MediaOss> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MediaOss> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getKeyword()), MediaOss::getCategoryName, queryDto.getKeyword());
        wrapper.in(CollUtil.isNotEmpty(ids), MediaOss::getCategoryId, ids);
        IPage<MediaOss> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MediaOssVO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MediaOssDeleteDTO deleteDTO) {


        List<Long> ossIds = deleteDTO.getOssIds();
        // remove-oss
        List<MediaOss> mediaOsses = this.listByIds(ossIds);
        for(MediaOss mediaOss : mediaOsses){
            String ossUrl = mediaOss.getOssUrl();

            DeleteObjectRequest req = DeleteObjectRequest.newBuilder()
                    .bucket(ossProperties.getBucket())
                    .key(ossUrl.replace(ossProperties.getDomain(),""))
                    .build();
            ossClient.deleteObject(req);
        }

        // remove-db
        this.removeByIds(ossIds);


    }


}
