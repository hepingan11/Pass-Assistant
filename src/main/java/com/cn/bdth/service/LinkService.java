package com.cn.bdth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cn.bdth.dto.LinkDto;
import com.cn.bdth.vo.admin.LinkVo;

import java.util.List;

public interface LinkService {
    void allowLink(Long LinkId);

    void deleteLink(Long id);

    IPage<LinkVo> getLinkList(int num,String prompt);

    List<LinkVo> getPersonalLinkList();

    void setTopImg(String imgUrl);

    String getTopImg();

    List<LinkVo> getUserLinkList();

    void applyLink(LinkDto linkDto);

    List<LinkVo> getAllLinkList(String linkName);

    void refuseLink(Long id);

    void setLinkHot(Long id);

    void deleteLinkHot(Long id);

    void addStatLink(Long id);

    List<LinkVo> selectStatLink();

    void cancelStatLink(Long id);

    LinkVo selectStatLinkById(Long id);
}
