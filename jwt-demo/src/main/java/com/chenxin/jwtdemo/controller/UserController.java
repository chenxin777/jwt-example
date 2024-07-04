package com.chenxin.jwtdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenxin.jwtdemo.common.BaseResponse;
import com.chenxin.jwtdemo.common.ErrorCode;
import com.chenxin.jwtdemo.common.ResultUtils;
import com.chenxin.jwtdemo.constant.UserConstant;
import com.chenxin.jwtdemo.exception.BusinesssException;
import com.chenxin.jwtdemo.model.domain.User;
import com.chenxin.jwtdemo.model.request.UserLoginRequest;
import com.chenxin.jwtdemo.model.request.UserRegisterRequest;
import com.chenxin.jwtdemo.service.UserService;
import com.chenxin.jwtdemo.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author fangchenxin
 * @description 用户管理
 * @date
 * @modify
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * @param userRegisterRequest
     * @return java.lang.Long
     * @description 用户注册
     * @author fangchenxin
     * @date 2024/4/19 21:05
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR);
        }
        long res = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(res);
    }

    /**
     * @param userLoginRequest
     * @param response
     * @return com.chenxin.usercenterserver.model.domain.User
     * @description 用户登陆
     * @author fangchenxin
     * @date 2024/4/19 21:05
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, response);
        return ResultUtils.success(user);
    }

    /**
     * @return com.chenxin.usercenterserver.model.domain.User
     * @description 获取当前用户
     * @author fangchenxin
     * @date 2024/4/19 21:05
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser() {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinesssException(ErrorCode.NOT_LOGIN);
        }
        return ResultUtils.success(currentUser);
    }

    /**
     * @param request
     * @return java.lang.Integer
     * @description 用户注销
     * @author fangchenxin
     * @date 2024/4/19 21:03
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("token", null);
        return ResultUtils.success(userService.userLogout(request));
    }

    /**
     * @param username
     * @return java.util.List<com.chenxin.usercenterserver.model.domain.User>
     * @description 查找用户
     * @author fangchenxin
     * @date 2024/4/19 21:04
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(@RequestParam(required = false) String username) {
        User currentUser = TokenUtils.getCurrentUser();
        if (UserConstant.ADMIN_ROLE != currentUser.getUserRole()) {
            throw new BusinesssException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (!StringUtils.isBlank(username)) {
            userQueryWrapper.like("username", username);
        }
        List<User> userList = userService.list(userQueryWrapper);
        return ResultUtils.success(userList.stream().map(userService::getSafeUser).collect(Collectors.toList()));
    }
}