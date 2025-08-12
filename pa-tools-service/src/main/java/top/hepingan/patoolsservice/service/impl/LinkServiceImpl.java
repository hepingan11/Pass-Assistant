package top.hepingan.patoolsservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.hepingan.pacommon.constants.ServerConstant;
import top.hepingan.pacommon.dto.LinkDto;
import top.hepingan.pacommon.entity.Link;
import top.hepingan.pacommon.mapper.LinkMapper;
import top.hepingan.pacommon.utils.AliUploadUtils;
import top.hepingan.pacommon.utils.RedisUtils;
import top.hepingan.pacommon.utils.StringUtils;
import top.hepingan.pacommon.utils.UserUtils;
import top.hepingan.pacommon.vo.admin.LinkVo;
import top.hepingan.patoolsservice.enums.FileEnum;
import top.hepingan.patoolsservice.service.LinkService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkServiceImpl implements LinkService {

    private final LinkMapper linkMapper;

    private final AliUploadUtils aliUploadUtils;

    private final RedisUtils redisUtils;

    /**
     * 通过链接
     * @param LinId
     */
    @Override
    public void allowLink(Long LinId) {
        linkMapper.setIsPublicById(LinId);
    }

    /**
     * 删除链接
     * @param id
     */
    @Override
    public void deleteLink(Long id) {
        linkMapper.deleteById(id);
    }

    /**
     * 旧的获取社区链接列表
     * @return
     */
    @Override
    public IPage<LinkVo> getLinkList(int num, String prompt) {

        return linkMapper.selectPage(new Page<>(num, 20), new QueryWrapper<Link>()
                .lambda()
                .eq(Link::getIsPublic, 0L)
                .like(StringUtils.notEmpty(prompt), Link::getLinkName, prompt)
                .select(
                        Link::getLinkId,
                        Link::getLinkUrl,
                        Link::getLinkImg,
                        Link::getLinkName,
                        Link::getLinkIntro
                ).orderByDesc(Link::getCreatedTime)
        ).convert(o -> new LinkVo()
                .setLinkId(o.getLinkId())
                .setLinkUrl(o.getLinkUrl())
                .setLinkName(o.getLinkName())
                .setLinkIntro(o.getLinkIntro())
                .setLinkImg(o.getLinkImg()));

    }

    /**
     * 获取个人链接列表
     * @param
     * @return
     */
    @Override
    public List<LinkVo> getPersonalLinkList() {
        return linkMapper.selectList(new QueryWrapper<Link>()
                .lambda()
                .eq(Link::getUserId, UserUtils.getCurrentLoginId())
                .select(Link::getLinkId,Link::getLinkName,Link::getIsPublic,Link::getLinkIntro,Link::getLinkUrl,Link::getLinkImg)
        ).stream().map(link -> {
            LinkVo linkVo = new LinkVo()
                    .setLinkName(link.getLinkName())
                    .setLinkUrl(link.getLinkUrl())
                    .setLinkId(link.getLinkId())
                    .setIsPublic(link.getIsPublic())
                    .setLinkImg(link.getLinkImg())
                    .setLinkIntro(link.getLinkIntro());
            return linkVo;
        }).collect(Collectors.toList());
    }

    @Override
    public void setTopImg(String imgUrl) {
        redisUtils.setValue(ServerConstant.LINK_TOP_IMG,imgUrl);
    }

    @Override
    public String getTopImg() {
        final String value = redisUtils.getValue(ServerConstant.LINK_TOP_IMG).toString();
        return value;
    }

    @Override
    public List<LinkVo> getUserLinkList() {
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        return linkMapper.selectList(new QueryWrapper<Link>().lambda()
                .eq(Link::getUserId,currentLoginId)
                .select(Link::getLinkId,Link::getLinkName,Link::getIsPublic,Link::getLinkIntro,Link::getLinkUrl,Link::getLinkImg,Link::getLinkSort,Link::getIsHot)
                ).stream().map(link ->{
                    LinkVo linkvo =new LinkVo()
                            .setLinkName(link.getLinkName())
                            .setLinkUrl(link.getLinkUrl())
                            .setLinkId(link.getLinkId())
                            .setIsPublic(link.getIsPublic())
                            .setLinkImg(link.getLinkImg())
                            .setLinkIntro(link.getLinkIntro())
                            .setIsHot(link.getIsHot())
                            .setLinkSort(link.getLinkSort());
                    return linkvo;
        }).collect(Collectors.toList());
    }

    /**
     * 申请链接
     * @param linkDto
     */
    @Override
    public void applyLink(LinkDto linkDto) {
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        final String fileName = UUID.randomUUID() + ".jpg";
        String url=aliUploadUtils.uploadFile(linkDto.getImages(), FileEnum.LINK.getDec(),fileName,true);
        Link link =new Link()
                .setLinkUrl(linkDto.getLinkUrl())
                .setLinkName(linkDto.getLinkName())
                .setLinkIntro(linkDto.getLinkIntro())
                .setIsPublic(0L)
                .setLinkSort(linkDto.getLinkSort())
                .setUserId(UserUtils.getCurrentLoginId())
                .setLinkImg(url).setIsHot(0L).setLinkId(2L)
                .setCreatedTime(LocalDateTime.now());
        linkMapper.insertLink(link);
    }

    @Override
    public List<LinkVo> getAllLinkList(String linkName) {
        LambdaQueryWrapper<Link> linkLambdaQueryWrapper = new QueryWrapper<Link>().lambda()
                .select(Link::getLinkId, Link::getLinkName, Link::getIsPublic, Link::getLinkIntro, Link::getLinkUrl, Link::getLinkImg, Link::getLinkSort, Link::getIsHot)
                .orderByDesc(Link::getCreatedTime);
        if (linkName != null&& !linkName.isEmpty()){
            linkLambdaQueryWrapper.like(Link::getLinkName,linkName);
        }
        List<LinkVo> list =linkMapper.selectList(linkLambdaQueryWrapper
        ).stream().map(link ->{
            LinkVo linkvo =new LinkVo()
                    .setLinkName(link.getLinkName())
                    .setLinkUrl(link.getLinkUrl())
                    .setLinkId(link.getLinkId())
                    .setIsPublic(link.getIsPublic())
                    .setLinkImg(link.getLinkImg())
                    .setLinkIntro(link.getLinkIntro())
                    .setLinkSort(link.getLinkSort())
                    .setIsHot(link.getIsHot());
            return linkvo;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public void refuseLink(Long id) {
        linkMapper.deleteIsPublicById(id);
    }

    @Override
    public void setLinkHot(Long id) {
        linkMapper.setIsHot(id);
    }

    @Override
    public void deleteLinkHot(Long id) {
        linkMapper.deleteIsHot(id);
    }

    @Override
    public void addStatLink(Long id) {
        Long userId = UserUtils.getCurrentLoginId();
        linkMapper.addStatLinkById(id,userId);
    }

    @Override
    public List<LinkVo> selectStatLink() {
        Long userId = UserUtils.getCurrentLoginId();
        List<Long> linkIdList = linkMapper.selectStatLink(userId);
        List<LinkVo> list = new ArrayList<>();
        for (Long id : linkIdList){
            final Link link = linkMapper.selectLinkById2(id);
            list.add(new LinkVo()
                            .setLinkId(link.getLinkId())
                    .setLinkName(link.getLinkName())
                    .setLinkId(link.getLinkId())
                    .setLinkUrl(link.getLinkUrl())
                    .setLinkImg(link.getLinkImg())
                    .setLinkIntro(link.getLinkIntro())
                    .setIsPublic(link.getIsPublic())
                    .setIsHot(link.getIsHot()));
//            Link link =linkMapper.selectById(id);
//            LinkVo linkVo = new LinkVo()
//                    .setLinkId(id)
//                    .setLinkName(link.getLinkName())
//                    .setLinkId(link.getLinkId())
//                    .setLinkUrl(link.getLinkUrl())
//                    .setLinkImg(link.getLinkImg())
//                    .setLinkIntro(link.getLinkIntro())
//                    .setIsPublic(link.getIsPublic())
//                    .setIsHot(link.getIsHot());
//            list.add(linkVo);

        }
        return list;
    }

    @Override
    public void cancelStatLink(Long id) {
        Long userId = UserUtils.getCurrentLoginId();
        linkMapper.cancelStatLinkById(id,userId);
    }

    @Override
    public LinkVo selectStatLinkById(Long id) {
        Long userId = UserUtils.getCurrentLoginId();
        if (linkMapper.selectStatLinkById(userId,id)){

        }
        return null;
    }

}
