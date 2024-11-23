package myaong.popolog.apigatewayservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/test")
    public void test(@Value("${jwt.secret-key}") String jwtSecret) {
        log.info("jwtSecret = {}", jwtSecret);
    }
}
