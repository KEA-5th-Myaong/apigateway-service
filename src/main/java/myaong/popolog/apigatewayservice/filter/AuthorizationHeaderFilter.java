package myaong.popolog.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import myaong.popolog.apigatewayservice.jwt.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JwtUtil jwtUtil;

    public AuthorizationHeaderFilter(JwtUtil jwtUtil) {
        super(AuthorizationHeaderFilter.Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // access token 추출
            ServerHttpRequest request = exchange.getRequest();
            String accessToken = jwtUtil.getTokenFromHeader(request, "Authorization");
            log.info("Access token: {}", accessToken);

            // JWT 검증
            if (jwtUtil.validateToken(accessToken)) {
                // 검증 성공 시 로직
                Long memberId = jwtUtil.getMemberId(accessToken);
                request.mutate().header("memberId", String.valueOf(memberId));
                log.info("Request Member Id : {}", memberId);
                return chain.filter(exchange);
            }

            // 인증 실패 시 401 에러 반환
            return onError(exchange, "AccessToken이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        };
    }

    // 오류 처리 메서드
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // JSON 응답 생성
        String jsonResponse = String.format("{\"message\":\"%s\",\"status\":%d}", message, httpStatus.value());

        // 응답 작성
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(jsonResponse.getBytes())));
    }
}
