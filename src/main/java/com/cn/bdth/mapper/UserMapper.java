package com.cn.bdth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cn.bdth.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("update user set frequency=frequency - 8  where user_id = #{id}")
    void updateFrequencyById(Long id);

    @Select("select frequency from user where user_id = #{id}")
    Long getFrequencyById(Long id);

    @Update("update user set user_name = #{username} where user_id = #{id}")
    void updateUserName(Long id,String username);
}
