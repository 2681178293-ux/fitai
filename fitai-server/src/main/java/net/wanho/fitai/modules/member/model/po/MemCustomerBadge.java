package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class MemCustomerBadge implements Serializable {

    @TableId
    private Long id;
    private Long customerId;
    private Long badgeId;
    private LocalDateTime unlockTime;
    private String isDisplayed;
}
