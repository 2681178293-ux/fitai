package net.wanho.fitai.modules.media.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.exception.CustomException;
import net.wanho.fitai.modules.media.code.MediaCode;
import net.wanho.fitai.modules.media.mapper.MediaVodCategoryMapper;
import net.wanho.fitai.modules.media.mapper.MediaVodMapper;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.po.MediaVod;
import net.wanho.fitai.modules.media.model.po.MediaVodCategory;
import net.wanho.fitai.modules.media.model.vo.MediaVodCategoryVO;
import net.wanho.fitai.modules.media.service.MediaVodCategoryServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaVodCategoryServiceImpl extends MPJBaseServiceImpl<MediaVodCategoryMapper, MediaVodCategory> implements MediaVodCategoryServiceI {

    private final Converter converter;
    private final MediaVodMapper mediaVodMapper;

    @Override
    public IPage<MediaVodCategoryVO> findByPage(MediaVodCategoryQueryDTO queryDto) {

        IPage<MediaVodCategory> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MediaVodCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCategoryName()), MediaVodCategory::getCategoryName, queryDto.getCategoryName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), MediaVodCategory::getStatus, queryDto.getStatus());
        IPage<MediaVodCategory> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MediaVodCategoryVO.class));

    }

    @Override
    public void add(MediaVodCategoryAddDTO addDto) {
        MediaVodCategory mediavodcategory = converter.convert(addDto, MediaVodCategory.class);
        this.save(mediavodcategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MediaVodCategoryUpdateDTO updateDto) {
        // 不能重复
        MediaVodCategory one = this.getOne(new LambdaQueryWrapper<MediaVodCategory>().eq(MediaVodCategory::getCategoryName, updateDto.getCategoryName()));
        if (ObjectUtil.isNotEmpty(one)) {
            throw new CustomException(MediaCode.MEDIA_CATEGORY_EXISTS);
        }

        // 修改vodCategory
        MediaVodCategory mediaVodCategory = converter.convert(updateDto, MediaVodCategory.class);
        this.updateById(mediaVodCategory);

        // 修改vod
        LambdaUpdateWrapper<MediaVod> wrapper = new LambdaUpdateWrapper<MediaVod>()
                .eq(MediaVod::getCategoryId, updateDto.getCategoryId())
                .set(MediaVod::getCategoryName, updateDto.getCategoryName());
        mediaVodMapper.update(wrapper);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MediaVodCategoryVO findById(Long id) {
        MediaVodCategory entity = this.getById(id);
        return converter.convert(entity, MediaVodCategoryVO.class);
    }

    @Override
    public List<MediaVodCategoryVO> getCategoryTree() {
        Map<Long, Long> countMap = countVideosByCategory();

        List<MediaVodCategoryVO> all = this.list(new LambdaQueryWrapper<MediaVodCategory>()
                        .orderByAsc(MediaVodCategory::getParentId)
                        .orderByAsc(MediaVodCategory::getOrderNum)
                )
                .stream()
                .map(item -> {
                    MediaVodCategoryVO vodCategoryVO = converter.convert(item, MediaVodCategoryVO.class);
                    vodCategoryVO.setVideoCount(ObjectUtil.defaultIfNull(countMap.get(item.getCategoryId()), 0L));
                    return vodCategoryVO;
                })
                .toList();

        return recursion(0L, all);
    }

    @Override
    public void moveCategory(MediaVodCategoryMoveDTO moveDto) {
        Long categoryId = moveDto.getCategoryId();
        Long targetCategoryId = moveDto.getTargetCategoryId();

        MediaVodCategory current = this.getById(categoryId);
        Integer currentOrderNum = current.getOrderNum();
        MediaVodCategory target = this.getById(targetCategoryId);

        current.setOrderNum(target.getOrderNum());
        target.setOrderNum(currentOrderNum);
        this.updateById(current);
        this.updateById(target);
    }

    @Override
    public void addCategory(MediaVodCategorySimpleAddDTO addDto) {
        MediaVodCategory mediaVodCategory = converter.convert(addDto, MediaVodCategory.class);

        Long parentId = ObjectUtil.defaultIfNull(addDto.getParentId(), 0L);
        // 把parentId获作为条件，获取orderNum的最大值
        LambdaQueryWrapper<MediaVodCategory> wrapper = new LambdaQueryWrapper<MediaVodCategory>()
                .eq(MediaVodCategory::getParentId, parentId)
                .orderByDesc(MediaVodCategory::getOrderNum);

        List<MediaVodCategory> children = this.list(wrapper);
        Integer maxOrderNum=0;
        if(CollUtil.isNotEmpty(children)) {
            maxOrderNum = children.stream().map(item -> item.getOrderNum()).max(Integer::compareTo).get();
        }
        mediaVodCategory.setOrderNum(maxOrderNum + 1); // 设置新的orderNum


        if(parentId.equals(0L)) {
            mediaVodCategory.setAncestors("0");
        }else{
            MediaVodCategory parent = this.getById(parentId);
            mediaVodCategory.setAncestors(parent.getAncestors() + "," + parentId); // 设置新的祖先列表
        }

        this.save(mediaVodCategory);
    }

    @Override
    public void deleteCategory(String categoryId) {
        List<MediaVodCategory> children = this
                .list(new LambdaQueryWrapper<MediaVodCategory>()
                        .apply("find_in_set({0},ancestors)", categoryId));
        if(ObjectUtil.isNotEmpty(children)){
            throw new CustomException(MediaCode.MEDIA_CATEGORY_HAS_CHILDREN); // 如果有子分类，不能删除
        }
        this.removeById(categoryId);
    }

    private List<MediaVodCategoryVO> recursion(long parentId, List<MediaVodCategoryVO> all) {
        return all.stream()
                .filter(item -> item.getParentId().equals(parentId))
                .peek(item -> {
                    List<MediaVodCategoryVO> children = recursion(item.getCategoryId(), all);
                    item.setChildren(children);
                    if(CollUtil.isNotEmpty(children)){
                        long sum = children.stream().mapToLong(MediaVodCategoryVO::getVideoCount).sum();
                        item.setVideoCount(item.getVideoCount() + sum);
                    }
                })
                .toList();
    }

    private Map<Long, Long> countVideosByCategory() {

        QueryWrapper<MediaVod> wrapper = new QueryWrapper<>();
        wrapper.select("category_id", "count(0) as cnt")
                .groupBy("category_id");
        return mediaVodMapper.selectMaps(wrapper)
                .stream()
                .collect(Collectors.toMap(
                                item -> Long.valueOf(item.get("category_id").toString()),
                                item -> Long.valueOf(item.get("cnt").toString())
                        )
                );
    }
}
