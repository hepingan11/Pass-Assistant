package top.hepingan.pacommon.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DigitalVo {

    private String videoUrl;

    private String videoName;

    private String imgUrl;
}
