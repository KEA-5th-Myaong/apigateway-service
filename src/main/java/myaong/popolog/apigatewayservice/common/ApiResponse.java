package myaong.popolog.apigatewayservice.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Getter
public class ApiResponse<T> {

	private final boolean isSuccess;
	private final String code;
	private final String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final T data;

	private ApiResponse(boolean isSuccess, String code, String message, T data) {
		this.isSuccess = isSuccess;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	// 성공 응답
	public static <T> ApiResponse<T> onSuccess(T result) {
		return new ApiResponse<>(true, ApiCode.OK.getCode(), ApiCode.OK.getMessage(), result);
	}

	// 실패 응답
	public static <T> ApiResponse<T> onFailure(ApiCode status) {
		return new ApiResponse<>(false, status.getCode(), status.getMessage(), null);
	}

	// 실패 응답인데 errors가 필요한 경우
	public static <T> ApiResponse<T> onFailure(ApiCode status, T errors) {
		return new ApiResponse<>(false, status.getCode(), status.getMessage(), errors);
	}

	// handleExceptionInternal override에서 사용
	public static <T> ApiResponse<T> onFailure(int code, String message) {
		return new ApiResponse<>(false, "COMMON_"+code+"0", message, null);
	}

	// filter에서 사용
	// 오류 처리 메서드
	public static Mono<Void> responseOnFilter(ServerHttpResponse response, HttpStatus httpStatus, String code, String message, boolean success) {
		response.setStatusCode(httpStatus);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON); // JSON 콘텐츠 타입 설정

		ApiResponse responseMap = new ApiResponse<>(success, code, message, null);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String jsonResponse;

		try {
			jsonResponse = objectMapper.writeValueAsString(responseMap);
		} catch (IOException e) {
			// JSON 변환 중 오류 발생 시 기본 메시지 설정
			jsonResponse = "{\"message\":\"Internal Server Error\",\"status\":500}";
		}

		// 응답 작성
		return response.writeWith(Mono.just(response.bufferFactory().wrap(jsonResponse.getBytes())));
	}
}
