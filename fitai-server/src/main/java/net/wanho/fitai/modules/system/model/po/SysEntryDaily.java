package net.wanho.fitai.modules.system.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName("sys_entry_daily")
public class SysEntryDaily implements Serializable {

    private LocalDate statDate;
    private String channel;
    private Integer visitCount;
}
