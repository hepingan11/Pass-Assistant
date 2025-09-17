package com.cn.bdth.structure;


import com.cn.bdth.model.GptModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DeepseekRequestStructure {

    private String model;

    private List<GptModel.Messages> messages;

    private boolean stream = true;


}
