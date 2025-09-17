package com.cn.bdth.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DoubaoVideoCallback {

    private String id;

    private String model;

    private String status;

    private Content content;

    private Integer seed;

    private String resolution;

    private Integer duration;

    private String ratio;

    private Usage usage;

    private Integer framespersecond;

    @Data
    @Accessors(chain = true)
    public static class Content{
        private String video_url;

        private String last_frame_url;
    }

    @Data
    @Accessors(chain = true)
    public static class Usage{
        private Integer completion_tokens;

        private Integer total_tokens;
    }
}
