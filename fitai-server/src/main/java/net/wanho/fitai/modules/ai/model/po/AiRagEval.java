package net.wanho.fitai.modules.ai.model.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AiRagEval implements Serializable {

    @TableId
    private Long evalId;
    private Long knowledgeId;
    private String evalName;
    private String question;
    private String expectedAnswer;
    private String actualAnswer;
    private BigDecimal recallScore;
    private BigDecimal faithfulness;
    private BigDecimal relevancyScore;
    private BigDecimal correctness;
    private Integer latencyMs;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
