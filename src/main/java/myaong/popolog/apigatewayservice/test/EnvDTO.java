package myaong.popolog.apigatewayservice.test;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnvDTO {

    private String reissueUri;
    private String jwtSecretKey;
    private String mainUri;
    private String loginUri;
    private String profileFormUri;
}
