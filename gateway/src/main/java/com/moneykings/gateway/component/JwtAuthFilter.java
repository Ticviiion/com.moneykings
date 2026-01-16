package com.moneykings.gateway.component;

import com.moneykings.gateway.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {
    @Resource
    private JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 白名单路径（不校验 Token）
    @Value("#{'${auth.whitelist}'.split(',')}")
    private List<String> whitelist;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("请求路径: {}", path);
        log.info("白名单列表: {}", whitelist);

        // 检查是否在白名单
        if (isWhitelisted(path)) {
            log.info("路径 {} 在白名单中，跳过认证", path);
            return chain.filter(exchange);
        }
        log.info("路径 {} 需要认证", path);

        // 获取 Token
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        log.info("Authorization Header: {}", authHeader);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token缺失或格式错误");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7); // 去掉 "Bearer "
        log.info("Token: {}", token);
        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 将用户名放入 Header，传递给下游服务
        String userId = jwtUtil.getUserIdFromToken(token);
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Id", userId) // 或 X-User-Name
                        .build())
                .build();

        return chain.filter(modifiedExchange);

    }

    @Override
    public int getOrder() {
        return -200;
    }

    public boolean isWhitelisted(String path) {
        return whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
