package com.park.common.filter;

import com.park.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证过滤器
 *
 * @author park-team
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    /**
     * 请求预处理 - 验证 Token
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取 Token
        String authHeader = request.getHeader(tokenHeader);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(tokenPrefix)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未认证，请先登录\",\"data\":null}");
            return false;
        }

        // 解析 Token
        String token = authHeader.substring(tokenPrefix.length());
        Claims claims = jwtUtil.parseToken(token);

        if (claims == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\",\"data\":null}");
            return false;
        }

        // 将用户信息存入请求属性
        request.setAttribute("userId", claims.get("userId"));
        request.setAttribute("username", claims.getSubject());

        // 解析角色类型（存储为字符串，需要转换为Integer）
        Object roleObj = claims.get("role");
        if (roleObj != null) {
            try {
                Integer roleType = Integer.valueOf(roleObj.toString());
                request.setAttribute("roleType", roleType);
            } catch (NumberFormatException e) {
                log.warn("角色类型格式错误：{}", roleObj);
            }
        }

        return true;
    }
}
