package com.danyl.xunwu.security;

import com.google.common.collect.ImmutableMap;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 基于角色的登陆入口控制器
 */
public class LoginUrlEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private PathMatcher pathMatcher = new AntPathMatcher();

    private final Map<String, String> authEntryPointMap;

    public LoginUrlEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
        authEntryPointMap = new ImmutableMap.Builder<String, String>()
                // 普通用户登陆入口映射
                .put("/user/**", "/user/login")
                // 管理员登陆入口映射
                .put("/admin/**", "/admin/login")
                .build();
    }

    /**
     * 根据请求跳转到指定的页面，父类是默认使用loginFormUrl
     */
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");
        for (Map.Entry<String, String> entry : this.authEntryPointMap.entrySet()) {
            if (this.pathMatcher.match(entry.getKey(), uri)) {
                return entry.getValue();
            }
        }
        return super.determineUrlToUseForThisRequest(request, response, exception);
    }
}
