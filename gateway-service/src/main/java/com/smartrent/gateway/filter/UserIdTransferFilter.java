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

        // 如果已登录，将userId写入请求头
        // 注意：Sa-Token 的上下文由 SaReactorFilter 建立；在上下文尚未就绪
        // （例如白名单放行路径、或鉴权过滤器先于本过滤器执行）时，直接调用
        // StpUtil 可能抛异常。这里做安全降级，避免影响正常请求转发。
        try {
            if (StpUtil.isLogin()) {
                long userId = StpUtil.getLoginIdAsLong();
                ServerHttpRequest newRequest = request.mutate()
                        .header(HEADER_USER_ID, String.valueOf(userId))
                        .build();
                return chain.filter(exchange.mutate().request(newRequest).build());
            }
        } catch (Exception e) {
            // 上下文不可用或解析失败：不写入用户头，直接透传请求
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100; // 在路由转发之前执行
    }
}
