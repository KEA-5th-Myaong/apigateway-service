package myaong.popolog.apigatewayservice.test;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {

    private final Environment env;

    @GetMapping
    public EnvDTO getEnvVariable() {
        String jwtSecret = env.getProperty("jwt.secret-key");
        String reissueUri = env.getProperty("redirect-url.reissue");
        String mainPageUri = env.getProperty("redirect-url.main");
        String loginUri = env.getProperty("redirect-url.login");
        String profileFormUri = env.getProperty("redirect-url.profile-form");

        return EnvDTO.builder()
                .jwtSecretKey(jwtSecret)
                .reissueUri(reissueUri)
                .mainUri(mainPageUri)
                .loginUri(loginUri)
                .profileFormUri(profileFormUri)
                .build();
    }
}
