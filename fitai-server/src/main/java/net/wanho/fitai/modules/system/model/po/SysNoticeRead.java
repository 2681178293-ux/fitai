package net.wanho.fitai.modules.system.model.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysNoticeRead implements Serializable {

    @TableId
    private Long readId;
    private Long noticeId;
    private Long customerId;
    private LocalDateTime readTime;
}
