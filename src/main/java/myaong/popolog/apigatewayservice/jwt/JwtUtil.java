package myaong.popolog.apigatewayservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.apigatewayservice.common.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static myaong.popolog.apigatewayservice.common.Constants.*;

@Slf4j
@Component
public class JwtUtil {

    private SecretKey secretKey;
    private String reissueUrl;

    // application.yml에 있는 평문 secret key를 가져와 초기화하였다.
    // 여기서는 HS256으로 진행했다.
    public JwtUtil(@Value("${jwt.secret-key}") String secret,
                   @Value("${redirect-url.reissue}") String reissueUrl) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        this.reissueUrl = reissueUrl;
    }

    public String getTokenFromHeader(ServerHttpRequest request, String name) {
        String token = request.getHeaders().getFirst(name);

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            // Bearer 제거 <- oAuth2를 이용했다고 명시적으로 붙여주는 타입인데 JWT를 검증하거나 정보를 추출 시 제거해줘야한다.
            return token.substring(7);
        }

        return null; // 토큰이 없거나 비어있을 경우 null 반환
    }

    public String getTokenFromCookie(ServerHttpRequest request, String name) {
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();

        // 쿠키가 존재하는지 확인
        if (cookies != null && cookies.containsKey(name)) {
            HttpCookie cookie = cookies.getFirst(name);
            if (cookie != null) {
                return cookie.getValue(); // 쿠키의 값 반환
            }
        }
        return null; // 쿠키가 없거나 값이 없을 경우 null 반환
    }


    // memberId 추출
    public Long getMemberId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("memberId", Long.class);
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            return false;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
            return true;
        }
    }

    public Boolean isExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            expiration.before(new Date());
            return false;
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            return false;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
            return true;
        }
    }

    public void redirectReissueURI(ServerHttpResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.fromClientResponse(REFRESH_KEY_NAME, refreshToken)
                 .httpOnly(true)
                 .path("/")
                 .maxAge(86400)
                 .build();

        response.addCookie(cookie);
        response.setStatusCode(HttpStatus.FOUND);

        // 클라이언트는 서버의 응답을 받고 HTTP 상태 코드가 302 Found인 경우 자동으로 Location 헤더에 지정된 URL로 리다이렉트됨
        response.getHeaders().setLocation(URI.create(reissueUrl)); // Location 헤더 설정
    }

}
