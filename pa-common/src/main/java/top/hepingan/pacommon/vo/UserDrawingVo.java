package top.hepingan.pacommon.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Accessors(chain = true)
@Data
public class UserDrawingVo {


    private Long drawingId;

    private String generateUrl;

}
