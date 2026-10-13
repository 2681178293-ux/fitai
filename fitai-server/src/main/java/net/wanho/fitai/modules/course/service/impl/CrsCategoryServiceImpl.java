package net.wanho.fitai.modules.course.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.exception.CustomException;
import net.wanho.fitai.modules.course.code.CourseCode;
import net.wanho.fitai.modules.course.mapper.CrsCategoryMapper;
import net.wanho.fitai.modules.course.model.dto.CrsCategoryAddDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCategoryQueryDTO;
import net.wanho.fitai.modules.course.model.dto.CrsCategoryUpdateDTO;
import net.wanho.fitai.modules.course.model.po.CrsCategory;
import net.wanho.fitai.modules.course.model.vo.CrsCategoryVO;
import net.wanho.fitai.modules.course.service.CrsCategoryServiceI;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CrsCategoryServiceImpl extends MPJBaseServiceImpl<CrsCategoryMapper, CrsCategory> implements CrsCategoryServiceI {

    private final Converter converter;

    @Override
    public IPage<CrsCategoryVO> findByPage(CrsCategoryQueryDTO queryDto) {

        IPage<CrsCategory> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<CrsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getCategoryName()), CrsCategory::getCategoryName, queryDto.getCategoryName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), CrsCategory::getStatus, queryDto.getStatus());
        IPage<CrsCategory> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, CrsCategoryVO.class));

    }

    @Override
    public void add(CrsCategoryAddDTO addDto) {
        CrsCategory crscategory = converter.convert(addDto, CrsCategory.class);

        Long parentId = addDto.getParentId();
        if(parentId == null || parentId.equals(0L)){
            crscategory.setParentId(0L);
            crscategory.setAncestors("0");
        }else{
            CrsCategory parent = this.getById(parentId);
            crscategory.setParentId(parentId);
            crscategory.setAncestors(parent.getAncestors() + "," + parent.getCategoryId());
        }

        this.save(crscategory);
    }

    @Override
    public void update(CrsCategoryUpdateDTO updateDto) {
        //首先查询旧数据，防止更新不存在的数据；
        //如果不存在则抛出异常
        Long categoryId = updateDto.getCategoryId();
        CrsCategory old = this.getById(categoryId);
        if (old == null) {
            throw new CustomException(CourseCode.COURSE_NOT_FOUND);
        }

        // 未传 parentId：表示不改父级（例如只改 status）
        Long parentId = updateDto.getParentId() != null ? updateDto.getParentId() : old.getParentId();

        String oldAncestors = StrUtil.blankToDefault(old.getAncestors(), "0");
        String newAncestors;
        if (parentId == null || parentId.equals(0L)) {
            parentId = 0L;
            newAncestors = "0";
        } else {
            CrsCategory parent = this.getById(parentId);
            if (parent == null) {
                throw new CustomException(CourseCode.COURSE_NOT_FOUND);
            }
            newAncestors = parent.getAncestors() + "," + parentId;
        }

        updateDto.setParentId(parentId);
        updateDto.setAncestors(newAncestors);

        CrsCategory crscategory = converter.convert(updateDto, CrsCategory.class);
        this.updateById(crscategory);

        //维护子树            父级未变则无需维护子树 ancestors
        if (Objects.equals(oldAncestors, newAncestors)) {
            return;
        }

        List<CrsCategory> crsCategoryList = this.list(new LambdaQueryWrapper<CrsCategory>()
                .apply("FIND_IN_SET({0},ancestors) or category_id={0}", categoryId));
        String quotedOld = Pattern.quote(oldAncestors);
        List<CrsCategory> list = crsCategoryList.stream().map(item -> {
            CrsCategory crsCategory = new CrsCategory();
            crsCategory.setCategoryId(item.getCategoryId());
            String anc = StrUtil.blankToDefault(item.getAncestors(), "");
            crsCategory.setAncestors(anc.replaceFirst(quotedOld, newAncestors));
            return crsCategory;
        }).toList();
        this.updateBatchById(list);
    }

    @Override
    public void delete(String id) {
        List<CrsCategory> crsCategoryList = this.list(new LambdaQueryWrapper<CrsCategory>()
                .apply("FIND_IN_SET({0},ancestors)", id));
        if(CollectionUtil.isNotEmpty(crsCategoryList)){
            throw new CustomException(CourseCode.COURSE_HAS_CHILDRREN);
        }

        this.removeById(id);
    }









    @Override
    public CrsCategoryVO findById(Long id) {
        CrsCategory entity = this.getById(id);
        return converter.convert(entity, CrsCategoryVO.class);
    }

    @Override
    public List<CrsCategoryVO> tree() {
        LambdaQueryWrapper<CrsCategory> wrapper = new LambdaQueryWrapper<CrsCategory>()
                .orderByAsc(CrsCategory::getParentId)
                .orderByAsc(CrsCategory::getOrderNum)
                .orderByAsc(CrsCategory::getCategoryId);
        List<CrsCategory> crsCategories = this.list(wrapper);
        List<CrsCategoryVO> all = crsCategories.stream().map(item -> converter.convert(item, CrsCategoryVO.class)).toList();
        return recursion(0L,all);
    }

    private List<CrsCategoryVO> recursion(long parentId, List<CrsCategoryVO> all) {
        return all.stream()
                .filter(item->item.getParentId().equals(parentId))
                .peek(item->item.setChildren(recursion(item.getCategoryId(),all)))
                .toList();
    }
}
