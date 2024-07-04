package com.chenxin.jwtdemo.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chenxin.jwtdemo.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fangchenxin
 * @description 针对表【user】的数据库操作Service
 * @createDate 2024-04-16 19:54:04
 */
public interface UserService extends IService<User> {


    /**
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return long
     * @description 用户注册
     * @author fangchenxin
     * @date 2024/4/17 00:12
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return com.chenxin.usercenterserver.model.domain.User
     * @description 用户登陆
     * @author fangchenxin
     * @date 2024/4/17 12:14
     */
    User userLogin(String userAccount, String userPassword, HttpServletResponse response);

    /**
     * @param originUser
     * @return com.chenxin.usercenterserver.model.domain.User
     * @description 获取脱敏用户信息
     * @author fangchenxin
     * @date 2024/4/17 23:32
     */
    User getSafeUser(User originUser);

    /**
     * @param request
     * @return int
     * @description 注销账号
     * @author fangchenxin
     * @date 2024/4/19 20:43
     */
    int userLogout(HttpServletRequest request);

}
