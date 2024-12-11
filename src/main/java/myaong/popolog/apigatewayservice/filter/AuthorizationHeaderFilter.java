package myaong.popolog.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import myaong.popolog.apigatewayservice.common.ApiCode;
import myaong.popolog.apigatewayservice.common.ApiResponse;
import myaong.popolog.apigatewayservice.jwt.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static myaong.popolog.apigatewayservice.common.Constants.*;


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
            ServerHttpRequest request = exchange.getRequest();
            String accessToken = jwtUtil.getTokenFromHeader(request, AUTHORIZATION_HEADER);
            String refreshToken = jwtUtil.getTokenFromCookie(request, REFRESH_KEY_NAME);
            log.info("Access token: {}", accessToken);
            log.info("Refresh token: {}", refreshToken);

            // access token과 refresh token이 유효할 때
            if (jwtUtil.validateToken(accessToken) && !jwtUtil.isExpired(accessToken)) {
                // 검증 성공 시 로직
                Long memberId = jwtUtil.getMemberId(accessToken);
                request.mutate().header("memberId", String.valueOf(memberId));
                log.info("Request Member Id : {}", memberId);
                return chain.filter(exchange);
            }

            // 임시로 refresh 토큰 만료 검사 x
            if (jwtUtil.isExpired(accessToken) && jwtUtil.validateToken(accessToken)) {
                if (jwtUtil.validateToken(refreshToken)) {
                    jwtUtil.redirectReissueURI(exchange.getResponse(), refreshToken);
                    return Mono.empty(); // 리다이렉트 후 체인 진행을 멈춤
                }
            }

            // 인증 실패 시 401 에러 반환
            return ApiResponse.responseOnFilter(exchange.getResponse(), HttpStatus.UNAUTHORIZED, ApiCode.INVALID_TOKEN.getCode(), ApiCode.INVALID_TOKEN.getMessage(), false);
        };
    }
}
