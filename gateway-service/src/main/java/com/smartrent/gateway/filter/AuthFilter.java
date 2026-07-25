package com.smartrent.gateway.filter;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthFilter {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                // 白名单：不需要登录即可访问
                .addExclude(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/user/wx-login",
                        "/api/house/list",
                        "/api/house/search",
                        "/api/recommend/hot",
                        "/api/recommend/by-area"
                )
                .setAuth(obj -> {
                    SaRouter.match("/**", r -> {
                        StpUtil.checkLogin();
                        // 将当前登录用户ID写入请求头，传递给下游服务
                        // Gateway 通过 RequestHeader 转发
                    });
                })
                .setError(e -> "{\"code\":1001,\"msg\":\"未登录或Token已过期\",\"data\":null}");
    }
}
