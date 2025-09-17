package com.cn.bdth.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
public class LinkPageVo implements Serializable {

    private IPage<LinkPage> links;


    @Data
    public static class LinkPage {
        private Long linkId;

        private Long userId;

        private String linkName;

        private String linkUrl;

        private String linkIntro;

        private String linkImg;

        private Long isPublic;

        private Long isHot;

    }
}