package com.chenxin.jwtdemo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.chenxin.jwtdemo.model.domain.User;
import com.chenxin.jwtdemo.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author fangchenxin
 * @description
 * @date 2024/7/3 23:35
 * @modify
 */
@Component
public class TokenUtils {

    private static UserService staticUserService;

    @Resource
    private UserService userService;

    @PostConstruct
    public void setUserService() {
        staticUserService = userService;
    }

    /**
     * @description 生成token（payload：用户id）
     * @author fangchenxin
     * @date 2024/7/4 17:07
     * @param userId
     * @param sign
     * @return java.lang.String
     */
    public static String genToken(String userId, String sign) {
        return JWT.create().withAudience(userId)
                .withExpiresAt(DateUtils.addMinutes(new Date(), 1))
                .sign(Algorithm.HMAC256(sign));
    }

    /**
     * @description 根据token获取当前用户
     * @author fangchenxin
     * @date 2024/7/4 17:07
     * @return com.chenxin.jwtdemo.model.domain.User
     */
    public static User getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (!StringUtils.isBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getSafeUser(staticUserService.getById(Long.valueOf(userId)));
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }
}
