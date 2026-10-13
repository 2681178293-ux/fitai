package net.wanho.fitai.modules.member.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

@Data
public class MemLevelBenefit implements Serializable {

    @TableId
    private Long levelBenefitId;
    private Long levelId;
    private Long benefitId;
}
