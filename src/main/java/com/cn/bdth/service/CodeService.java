package com.cn.bdth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.bdth.dto.CodeDto;
import com.cn.bdth.entity.Code;
import com.cn.bdth.vo.AlipayPayCodeVo;
import com.cn.bdth.vo.CodeListVo;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CodeService extends IService<Code> {

    List<CodeListVo> codelist(String name,String language);

    void addCode(CodeDto code);

    void addLook(Long codeId);

    void orderBuy(Long codeId);

    AlipayPayCodeVo generatePayQrCode(Long codeId);

    String  paymentStatus(String orderNo);

    void updateCode(@Valid CodeDto codeDto);

    void updateCodeImage(Long codeId, MultipartFile image);

    void deleteCodeImage(String imageUrl);

    void passCode(Long id);

    void rejectCode(Long id);

    List<CodeListVo> allCode();

    List<CodeListVo> myCode();
}
