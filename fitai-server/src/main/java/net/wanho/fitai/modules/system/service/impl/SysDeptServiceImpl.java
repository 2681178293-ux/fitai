package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.exception.CustomException;
import net.wanho.fitai.modules.system.code.SystemCode;
import net.wanho.fitai.modules.system.mapper.SysDeptMapper;
import net.wanho.fitai.modules.system.model.dto.SysDeptAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysDeptQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysDeptUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysDept;
import net.wanho.fitai.modules.system.model.vo.SysDeptVO;
import net.wanho.fitai.modules.system.service.SysDeptServiceI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends MPJBaseServiceImpl<SysDeptMapper, SysDept> implements SysDeptServiceI {

    private final Converter converter;



    @Override
    public void add(SysDeptAddDTO addDto) {
        SysDept entity = converter.convert(addDto, SysDept.class);

        Long parentId = addDto.getParentId();
        if(parentId.equals(0L)) {
            entity.setAncestors("0");
        }else {
            SysDept parentDept = this.getById(parentId);
            entity.setAncestors(parentDept.getAncestors() + "," + parentDept.getDeptId());
        }
        this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDeptUpdateDTO updateDto) {

        Long deptId = updateDto.getDeptId();
        Long parentId = updateDto.getParentId();
        String oldAncestors=this.getById(deptId).getAncestors();

        String newAncestors;
        if(parentId.equals(0L)){
            newAncestors="0";
        }else {
            newAncestors = this.getById(parentId).getAncestors() + "," + parentId;
        }

        SysDept entity = converter.convert(updateDto, SysDept.class);
        this.updateById(entity);

        // 维护ancestors [自身+子部门]
        List<SysDept> sysDeptList = this.list(new LambdaQueryWrapper<SysDept>().apply("FIND_IN_SET({0},ancestors) or dept_id={0}", deptId));
        List<SysDept> list = sysDeptList.stream().map(item -> {
            SysDept sysDept = new SysDept();
            sysDept.setDeptId(item.getDeptId());
            sysDept.setAncestors(item.getAncestors().replaceFirst(oldAncestors, newAncestors));
            return sysDept;
        }).toList();
        this.updateBatchById( list);



    }

    @Override
    public void delete(String id) {

        // 判断是否有下级
        List<SysDept> sysDeptList = this.list(new LambdaQueryWrapper<SysDept>().apply("FIND_IN_SET({0},ancestors)", id));
        if (CollectionUtil.isNotEmpty(sysDeptList)){
            throw new CustomException(SystemCode.DEPT_HAS_CHILDREN);
        }

        this.removeById(id);
    }

    @Override
    public SysDeptVO findById(Long id) {
        SysDept entity = this.getById(id);
        return converter.convert(entity, SysDeptVO.class);
    }

    @Override
    public List<SysDeptVO> listTree(SysDeptQueryDTO queryDto) {

        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
     /*
     *  select * from sys_dept where
		 (FIND_IN_SET
		(dept_id,(select ancestors FROM sys_dept where dept_name like '%运营%'))
		or  dept_name like '%运营%' )
		and status=0*/
        String deptName = queryDto.getDeptName();
        String sql = " ( FIND_IN_SET ( dept_id, " +
                "(select GROUP_CONCAT(ancestors) FROM sys_dept where dept_name like '%" + deptName + "%')) " +
                "or dept_name like '%" + deptName + "%' ) ";
        wrapper.apply(StrUtil.isNotBlank(deptName),sql);
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysDept::getStatus, queryDto.getStatus());
        wrapper.orderByAsc(SysDept::getParentId).orderByAsc(SysDept::getOrderNum);
        List<SysDeptVO> all = this.list(wrapper).stream().map(item -> converter.convert(item, SysDeptVO.class)).toList();
        return recursion(0L, all);
    }

    private List<SysDeptVO> recursion(long parentId, List<SysDeptVO> all) {
        return all.stream()
                .filter(item -> item.getParentId().equals(parentId))
                .peek(item -> item.setChildren(recursion(item.getDeptId(), all)))
                .toList();
    }
}
