package myaong.popolog.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import myaong.popolog.apigatewayservice.jwt.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static myaong.popolog.apigatewayservice.common.Constants.AUTHORIZATION_HEADER;
import static myaong.popolog.apigatewayservice.common.Constants.REFRESH_KEY_NAME;

@Component
@Slf4j
public class OptionalAuthorizationFilter extends AbstractGatewayFilterFactory<OptionalAuthorizationFilter.Config> {

    private final JwtUtil jwtUtil;

    public OptionalAuthorizationFilter(JwtUtil jwtUtil) {
        super(OptionalAuthorizationFilter.Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String accessToken = jwtUtil.getTokenFromHeader(request, AUTHORIZATION_HEADER);
            String refreshToken = jwtUtil.getTokenFromCookie(request, REFRESH_KEY_NAME);
            log.info("Access token: {}", accessToken);

            // access 토큰이 유효하지 않으면 체인을 거치지 않음
            if (!jwtUtil.validateToken(accessToken)) {
                return chain.filter(exchange);
            }

            // access token이 유효할 때
            if (!jwtUtil.isExpired(accessToken)) {
                // 검증 성공 시 로직
                Long memberId = jwtUtil.getMemberId(accessToken);
                request.mutate().header("memberId", String.valueOf(memberId));
                log.info("Request Member Id : {}", memberId);
                return chain.filter(exchange);
            }

            // access 토큰 만료시
            if (jwtUtil.isExpired(accessToken)) {
                // 임시로 refresh 토큰 만료 검사 x
                if (jwtUtil.validateToken(refreshToken)) {
                    jwtUtil.redirectReissueURI(exchange.getResponse(), refreshToken);
                    return Mono.empty(); // 리다이렉트 후 체인 진행을 멈춤
                }
            }

            return chain.filter(exchange);
        };
    }
}
