package net.wanho.fitai.modules.system.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_role_dept")
public class SysRoleDept implements Serializable {

    private Long roleId;
    private Long deptId;
}
