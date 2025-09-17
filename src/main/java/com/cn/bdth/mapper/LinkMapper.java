package com.cn.bdth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cn.bdth.entity.Link;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LinkMapper extends BaseMapper<Link> {

    @Update("update link set is_public =1 where link_id= #{linkId}")
    void setIsPublicById(Long linkId);

    @Insert("insert into link (link_name, user_id,link_url,link_intro, is_public, link_img, is_hot, link_sort) " +
            "values(#{linkName},#{userId}, #{linkUrl}, #{linkIntro}, #{isPublic},#{linkImg}, #{isHot},#{linkSort})")
    void insertLink(Link link);

    @Update("update link set is_public =0 where link_id= #{linkId}")
    void deleteIsPublicById(Long linkId);

    @Update("update link set is_hot =1 where link_id= #{linkId}")
    void setIsHot(Long linkId);

    @Update("update link set is_hot =0 where link_id= #{linkId}")
    void deleteIsHot(Long linkId);

    @Insert("insert into link_stat (stat_id, user_id) values(#{id}, #{userId})")
    void addStatLinkById(Long id, Long userId);

    @Select("select stat_id from link_stat where user_id = #{userId} ")
    List<Long> selectStatLink(Long userId);

    @Delete("delete from link_stat where stat_id = #{id} and user_id = #{userId}")
    void cancelStatLinkById(Long id,Long userId);

    @Select("select * from link_stat where stat_id = #{id} and user_id = #{userId}")
    boolean selectStatLinkById(Long userId, Long id);

    @Select("select link_name,link_sort,link_img,link_intro,link_url,link_id,is_hot from link where link_id = #{id}")
    Link selectLinkById2(Long id);
}
