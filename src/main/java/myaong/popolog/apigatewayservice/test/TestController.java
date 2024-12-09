package myaong.popolog.apigatewayservice.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final Environment env;

    @GetMapping
    public void getEnvVariable() {
        String jwtSecret = env.getProperty("jwt.secret-key");
        String reissueUri = env.getProperty("redirect-url.reissue");
        String mainPageUri = env.getProperty("redirect-url.main");
        String loginUri = env.getProperty("redirect-url.login");
        String profileFormUri = env.getProperty("redirect-url.profile-form");
        String kakaoRedirectUri = env.getProperty("${spring.security.oauth2.client.registration.kakao.redirect-uri}");

        log.info("jwt.secret-key: {}", jwtSecret);
        log.info("redirect-url.reissue: {}", reissueUri);
        log.info("redirect-url.main: {}", mainPageUri);
        log.info("redirect-url.login: {}", loginUri);
        log.info("redirect-url.profile-form: {}", profileFormUri);
        log.info("spring.security.oauth2.client.registration.kakao.redirect-uri: {}", kakaoRedirectUri);
    }
}
