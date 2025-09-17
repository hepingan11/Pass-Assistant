package com.cn.bdth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.bdth.constants.OperateConstant;
import com.cn.bdth.dto.admin.UserPutDto;
import com.cn.bdth.entity.User;
import com.cn.bdth.enums.FileEnum;
import com.cn.bdth.mapper.OrdersMapper;
import com.cn.bdth.mapper.StarMapper;
import com.cn.bdth.mapper.UserMapper;
import com.cn.bdth.service.UserService;
import com.cn.bdth.utils.*;
import com.cn.bdth.vo.UserInfoVo;
import com.cn.bdth.vo.admin.UserDataVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final AliUploadUtils aliUploadUtils;

    private final UserMapper userMapper;

    private final RedisUtils redisUtils;
    private final static String SALT = "HuJiaXin";

    private final StarMapper starMapper;

    private final OrdersMapper ordersMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editUserAvatar(final MultipartFile file) {
        final String uri = aliUploadUtils.uploadFile(file, FileEnum.AVATAR.getDec(), null, true);
        userMapper.updateById(
                new User().setUserId(UserUtils.getCurrentLoginId())
                        .setAvatar(uri)
        );
    }

    @Override
    public void editUserName(final String username) {
        userMapper.updateUserName(UserUtils.getCurrentLoginId(),username);
    }

    @Override
    public UserInfoVo getCurrentUserInfo() {

        final User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getUserId, UserUtils.getCurrentLoginId())
                .select(User::getUserName, User::getAvatar, User::getFrequency, User::getOpenId, User::getType)
        );
        return BeanUtils.copyClassProperTies(user, UserInfoVo.class);

    }

    @Override
    public Long getTotalUsers() {
        return userMapper.selectCount(null);
    }

    @Override
    public Integer sign() {
        int result = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //将Date类型转换成String类型
        Date date=null;
        String time = sdf.format(date);
        log.info("转换后的时间:" + time);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
        LocalDateTime localTime = LocalDateTime.parse(time, dtf);
        log.info("当前的localTime:" + localTime);
        LocalDateTime startTime = LocalDate.now().atTime(0, 0, 0);
        log.info("startTime:" + startTime);
        LocalDateTime endTime = LocalDate.now().atTime(23, 59, 59);
        log.info("endTime:" + endTime);
        //如果小于今天的开始日期
        if (localTime.isBefore(startTime)) {

            /**判断是否小于昨天，小于昨天证明签到不连续，签到记录表签到连续次数设置为0*/
            Date newTime = new Date();
            //将下面的 理解成  yyyy-MM-dd 00：00：00 更好理解点
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String todayStr = format.format(newTime);
            Date today = null;
            try {
                today = format.parse(todayStr);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            //昨天 86400000=24*60*60*1000 一天  大于昨天 至少为前天
            if ((today.getTime() - date.getTime()) > 86400000) {
                result = 2;
                log.info("小于今天的开始日期,至少为前天的时间,连续签到终止");
            } else {
                result = 0;
                log.info("小于今天的开始日期,最后一次签到是昨天，连续签到未终止");
            }
        }
        //如果大于今天的开始日期，小于今天的结束日期
        if (localTime.isAfter(startTime) && localTime.isBefore(endTime)) {
            log.info("大于今天的开始日期，小于今天的结束日期");
            result = 1;
        }
        //如果大于今天的结束日期
        if (localTime.isAfter(endTime)) {
            log.info("大于今天的结束日期");
            result = 1;
        }

        return result;
    }

    @Override
    public IPage<UserDataVo> getUserPageVo(final int pageNum, final String prompt) {
        return userMapper.selectPage(new Page<>(pageNum, 15), new QueryWrapper<User>()
                .lambda()
                .like(StringUtils.notEmpty(prompt), User::getUserName, prompt)
                .or().like(StringUtils.notEmpty(prompt), User::getOpenId, prompt)
                .select(User::getFrequency, User::getUserName, User::getCreatedTime, User::getUserId, User::getEmail,User::getAvatar)
                .orderByDesc(User::getFrequency)
        ).convert(u -> {
            //设置用户最后功能操作时间
            final Object value = redisUtils.getValue(OperateConstant.USER_CALL_TIME + u.getUserId());
            final UserDataVo userDataVo = new UserDataVo()
                    .setEmail(u.getEmail())
                    .setUserId(u.getUserId())
                    .setUserName(u.getUserName())
                    .setFrequency(u.getFrequency())
                    .setAvatar(u.getAvatar())
                    .setCreatedTime(u.getCreatedTime());
            if (value != null) {
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.toString());
                    userDataVo.setLastOperationTime(String.valueOf(date.getTime()));
                } catch (ParseException e) {
                    log.error("转化时间戳异常");
                }
            }

            return userDataVo;
        });

    }

    @Override
    public void updateById(final UserPutDto dto) {

        userMapper.updateById(new User().setFrequency(dto.getFrequency()).setUserId(dto.getUserId()));

    }

}
