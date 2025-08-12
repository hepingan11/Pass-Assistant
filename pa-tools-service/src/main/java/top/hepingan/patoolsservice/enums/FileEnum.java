package top.hepingan.patoolsservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 文件夹名
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public enum FileEnum {

    //头像
    AVATAR("avatar"),
    //绘图
    PAINTING("painting"),
    //视频
    AUDIO("audio"),
    //链接
    LINK("link");
    //代码项目
    String dec;

}
