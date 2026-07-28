package com.smartrent.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 将当前登录用户ID通过请求头传递给下游服务
 */
@Component
public class UserIdTransferFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USER_ID = "X-User-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 注意：Sa-Token 的 StpUtil.isLogin()/getLoginIdAsLong() 依赖线程绑定的
        // Sa-Token 上下文，而 Gateway 运行在 WebFlux 响应式环境下，该上下文在
        // GlobalFilter 中并不可靠（始终为“未登录”），因此不能用 StpUtil.isLogin()。
        // 正确做法：直接从 Authorization 头取出裸 token，用 getLoginIdByToken 解析
        // 登录 ID（纯无状态计算，不依赖上下文），再注入 X-User-Id 给下游服务。
        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && !token.isBlank()) {
            try {
                Object loginId = StpUtil.getLoginIdByToken(token);
                if (loginId != null) {
                    long userId = Long.parseLong(loginId.toString());
                    ServerHttpRequest newRequest = request.mutate()
                            .header(HEADER_USER_ID, String.valueOf(userId))
                            .build();
                    return chain.filter(exchange.mutate().request(newRequest).build());
                }
            } catch (Exception e) {
                // token 无效或解析失败：不写入用户头，直接透传请求
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 必须在 SaReactorFilter（order=-100）之后执行
        return -99;
    }
}
