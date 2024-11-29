package myaong.popolog.apigatewayservice.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiCode {

	OK(HttpStatus.OK, "COMMON_2000", "OK"),
	INVALID_DATA(HttpStatus.BAD_REQUEST, "COMMON_4000", "Request data missing or invalid"),
	INVALID_TOKEN(HttpStatus.BAD_REQUEST, "COMMON_4010", "Invalid or expired token"),
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_4030", "Forbidden"),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_4050", "Method not allowed"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_5000", "Internal Server Error"),
	DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_5001", "DB Error"),

	RE_AUTHENTICATION_FAILURE(HttpStatus.BAD_REQUEST, "MEMBER_4000", "기존 비밀번호와 일치하지 않습니다."),
	INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_4001", "이메일 형식이 올바르지 않습니다."),
	INVALID_LOGIN_ID_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_4002", "아이디 형식이 올바르지 않습니다."),
	INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_4003", "비밀번호 형식이 올바르지 않습니다."),
	UNMATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER_4004", "비밀번호가 일치하지 않습니다."),

	INCORRECT_ID(HttpStatus.UNAUTHORIZED, "MEMBER_4010", "아이디가 맞지 않습니다. 다시 시도해주세요."),
	INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER_4011", "비밀번호가 맞지 않습니다. 다시 시도해주세요."),	// 재시도 횟수를 함께 전송
	UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "MEMBER_4012", "로그인이 필요한 요청입니다."),
	FAILED_LOGIN(HttpStatus.UNAUTHORIZED, "MEMBER_4013", "일치하는 회원 정보가 없습니다. 아이디 혹은 비밀번호를 다시 확인해주세요."),

	ACCESS_DENIED(HttpStatus.NOT_FOUND, "MEMBER_4030", "접근 불가능한 권한입니다."),

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_4040", "존재하지 않는 회원입니다."),
	ID_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_4041", "가입하지 않은 아이디입니다."),

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}