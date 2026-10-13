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
import net.wanho.fitai.modules.media.mapper.MediaOssCategoryMapper;
import net.wanho.fitai.modules.media.mapper.MediaOssMapper;
import net.wanho.fitai.modules.media.model.dto.*;
import net.wanho.fitai.modules.media.model.po.MediaOss;
import net.wanho.fitai.modules.media.model.po.MediaOssCategory;
import net.wanho.fitai.modules.media.model.vo.MediaOssCategoryVO;
import net.wanho.fitai.modules.media.service.MediaOssCategoryServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaOssCategoryServiceImpl extends MPJBaseServiceImpl<MediaOssCategoryMapper, MediaOssCategory> implements MediaOssCategoryServiceI {

    private final Converter converter;
    private final MediaOssMapper mediaOssMapper;


    @Override
    public IPage<MediaOssCategoryVO> findByPage(MediaOssCategoryQueryDTO queryDto) {

        IPage<MediaOssCategory> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<MediaOssCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCategoryName()), MediaOssCategory::getCategoryName, queryDto.getCategoryName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), MediaOssCategory::getStatus, queryDto.getStatus());
        IPage<MediaOssCategory> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, MediaOssCategoryVO.class));

    }

    @Override
    public void add(MediaOssCategoryAddDTO addDto) {
        MediaOssCategory mediaosscategory = converter.convert(addDto, MediaOssCategory.class);
        this.save(mediaosscategory);
    }

    @Override
    public void update(MediaOssCategoryUpdateDTO updateDto) {
        MediaOssCategory mediaosscategory = converter.convert(updateDto, MediaOssCategory.class);
        this.updateById(mediaosscategory);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        // 把ids 变成集合
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public MediaOssCategoryVO findById(Long id) {
        MediaOssCategory entity = this.getById(id);
        return converter.convert(entity, MediaOssCategoryVO.class);
    }

    @Override
    public List<MediaOssCategoryVO> tree() {
        Map<Long, Long> countMap = countImagesByCategory();

        List<MediaOssCategoryVO> all = this.list(new LambdaQueryWrapper<MediaOssCategory>()
                .orderByAsc(MediaOssCategory::getParentId)
                .orderByAsc(MediaOssCategory::getOrderNum)
        ).stream().map(item -> {
            MediaOssCategoryVO categoryVO = converter.convert(item, MediaOssCategoryVO.class);
            categoryVO.setImageCount(countMap.get(item.getCategoryId()));
            return categoryVO;
        }).toList();
        return recursion(0L, all);
    }

    @Override
    public void move(MediaOssCategoryMoveDTO moveDto) {
        Long categoryId = moveDto.getCategoryId();
        Long targetCategoryId = moveDto.getTargetCategoryId();
        String direction = moveDto.getDirection(); // "up"

        MediaOssCategory current = this.getById(categoryId);
        Integer currentOrderNum = current.getOrderNum();
        MediaOssCategory target = this.getById(targetCategoryId);

        current.setOrderNum(target.getOrderNum());
        target.setOrderNum(currentOrderNum);
        this.updateById(current);
        this.updateById(target);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryName(MediaOssCategoryUpdateNameDTO moveDto) {

        // 不能重复
        MediaOssCategory one = this.getOne(new LambdaQueryWrapper<MediaOssCategory>().eq(MediaOssCategory::getCategoryName, moveDto.getCategoryName()));
        if (ObjectUtil.isNotEmpty(one)) {
            throw new CustomException(MediaCode.MEDIA_CATEGORY_NOT_FOUND);
        }

        // 修改ossCategory
        MediaOssCategory mediaOssCategory = converter.convert(moveDto, MediaOssCategory.class);
        this.updateById(mediaOssCategory);

        // 修改oos
        LambdaUpdateWrapper<MediaOss> wrapper = new LambdaUpdateWrapper<MediaOss>()
                .eq(MediaOss::getCategoryId, moveDto.getCategoryId())
                .set(MediaOss::getCategoryName, moveDto.getCategoryName());
        mediaOssMapper.update(wrapper);

    }

    @Override
    public void addCategory(MediaOssCategorySimpleAddDTO addDto) {
        MediaOssCategory mediaOssCategory = converter.convert(addDto, MediaOssCategory.class);

        Long parentId = ObjectUtil.defaultIfNull(addDto.getParentId(), 0L);
        // 把parentId获作为条件，获取orderNum的最大值
        LambdaQueryWrapper<MediaOssCategory> wrapper = new LambdaQueryWrapper<MediaOssCategory>()
                .eq(MediaOssCategory::getParentId, parentId)
                .orderByDesc(MediaOssCategory::getOrderNum);

        List<MediaOssCategory> children = this.list(wrapper);
        Integer maxOrderNum=0;
        if(CollUtil.isNotEmpty(children)) {
             maxOrderNum = children.stream().map(item -> item.getOrderNum()).max(Integer::compareTo).get();
        }
        mediaOssCategory.setOrderNum(maxOrderNum + 1); // 设置新的orderNum


        if(parentId.equals(0L)) {
            mediaOssCategory.setAncestors("0");
        }else{
            MediaOssCategory parent = this.getById(parentId);
            mediaOssCategory.setAncestors(parent.getAncestors() + "," + parentId); // 设置新的祖先列表
        }

        this.save(mediaOssCategory);
    }

    @Override
    public void deleteCategory(String categoryId) {
        List<MediaOssCategory> children = this
                .list(new LambdaQueryWrapper<MediaOssCategory>()
                .apply("find_in_set({0},ancestors)", categoryId));
        if(ObjectUtil.isNotEmpty(children)){
            throw new CustomException(MediaCode.MEDIA_CATEGORY_HAS_CHILDREN); // 如果有子分类，不能删除
        }
        this.removeById(categoryId);
    }

    private List<MediaOssCategoryVO> recursion(long parentId, List<MediaOssCategoryVO> all) {
        return all.stream()
                .filter(item -> item.getParentId().equals(parentId))
                .peek(item -> {
                    List<MediaOssCategoryVO> children = recursion(item.getCategoryId(), all);
                    item.setChildren(children);
                    if (CollUtil.isNotEmpty(children)) {
                        long total = children.stream().mapToLong(c -> ObjectUtil.defaultIfNull(c.getImageCount(), 0L)).sum();
                        item.setImageCount(ObjectUtil.defaultIfNull(item.getImageCount(), 0L) + total);
                    }
                })
                .toList();
    }

    private Map<Long, Long> countImagesByCategory() {

        // SELECT category_id,count(0) cnt from media_oss GROUP BY category_id
        QueryWrapper<MediaOss> wrapper = new QueryWrapper<>();
        wrapper.select("category_id", "count(0) cnt").groupBy("category_id");
        return mediaOssMapper
                .selectMaps(wrapper)
                .stream()
                .collect(Collectors.toMap(
                        item -> Long.valueOf(item.get("category_id").toString()),
                        item -> Long.valueOf(item.get("cnt").toString())

                ));
    }
}
