package com.chenxin.jwtdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenxin.jwtdemo.common.ErrorCode;
import com.chenxin.jwtdemo.exception.BusinesssException;
import com.chenxin.jwtdemo.mapper.UserMapper;
import com.chenxin.jwtdemo.model.domain.User;
import com.chenxin.jwtdemo.service.UserService;
import com.chenxin.jwtdemo.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.chenxin.jwtdemo.constant.UserConstant.SALT;


/**
 * @author fangchenxin
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-04-16 19:54:04
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @param planetCode
     * @return long
     * @description
     * @author fangchenxin
     * @date 2024/4/19 20:44
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账户不包含特殊字符
        String validPattern = "^[A-Za-z0-9]*$";
        if (!userAccount.matches(validPattern)) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "用户账号包含特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "密码和校验密码不相同");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "用户账号重复");
        }

        // 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "星球编号重复");
        }

        // 2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3、插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername(userAccount);
        user.setPlanetCode(planetCode);
        boolean res = save(user);
        if (!res) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "注册失败");
        }
        return user.getId();
    }

    /**
     * @param userAccount
     * @param userPassword
     * @return com.chenxin.usercenterserver.model.domain.User
     * @description 登陆
     * @author fangchenxin
     * @date 2024/4/19 20:44
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletResponse response) {
        // 1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "登录参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账户不包含特殊字符
        String validPattern = "^[A-Za-z0-9]*$";
        if (!userAccount.matches(validPattern)) {
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "用户账号包含特殊字符");
        }
        // 2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询账户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinesssException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // token
        String token = TokenUtils.genToken(user.getId().toString(), user.getUserPassword());
        response.setHeader("token", token);
        response.setHeader("Access-Control-Expose-Headers","token");
        // 3、用户脱敏(返回前端能看到的数据)
        User safeUser = getSafeUser(user);
        return safeUser;

    }

    /**
     * @param originUser
     * @return com.chenxin.usercenterserver.model.domain.User
     * @description 获取脱敏用户
     * @author fangchenxin
     * @date 2024/4/19 20:45
     */
    @Override
    public User getSafeUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setGender(originUser.getGender());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setUserRole(originUser.getUserRole());
        safeUser.setPlanetCode(originUser.getPlanetCode());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setTags(originUser.getTags());
        return safeUser;
    }

    /**
     * @param request
     * @return int
     * @description 注销用户
     * @author fangchenxin
     * @date 2024/4/19 20:44
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        return -1;
    }

}




