package com.chenxin.jwtdemo.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.chenxin.jwtdemo.common.ErrorCode;
import com.chenxin.jwtdemo.exception.BusinesssException;
import com.chenxin.jwtdemo.model.domain.User;
import com.chenxin.jwtdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.util.Date;

/**
 * @author fangchenxin
 * @description
 * @date 2024/7/3 23:09
 * @modify
 */
@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        log.info("request {} with token: {}",  request.getRequestURL(), token);
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (StringUtils.isBlank(token)) {
            throw new BusinesssException(ErrorCode.CODE_401, "无token，请重新登录");
        }
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException ex) {
            throw new BusinesssException(ErrorCode.CODE_401, "token验证失败，请重新登录");
        }
        Date expiresAt = JWT.decode(token).getExpiresAt();
        if (expiresAt.before(new Date())) {
            throw new BusinesssException(ErrorCode.CODE_401, "token已过期，请重新登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinesssException(ErrorCode.CODE_401, "用户不存在");
        }
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getUserPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException ex) {
            throw new BusinesssException(ErrorCode.CODE_401, "token验证失败，请求重新登录");
        }

        return true;
    }

}
