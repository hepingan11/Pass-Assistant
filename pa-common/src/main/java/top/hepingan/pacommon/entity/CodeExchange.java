package top.hepingan.pacommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("code_exchange")
public class CodeExchange {

    @TableId(type = IdType.AUTO)
    private Long codeExchangeId;

    private String orderNo;

    private Long sellUserId;

    private Long buyUserId;

    private Long codeId;

    private Double price;

    private String status;

    private LocalDateTime createdTime;
}
