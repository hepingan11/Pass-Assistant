package com.cn.bdth.vo.admin;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LinkVo {
    private Long linkId;

    private String linkName;

    private String linkUrl;

    private String linkImg;

    private String linkIntro;

    private String linkSort;

    private Long isPublic;

    private Long isHot;

}
