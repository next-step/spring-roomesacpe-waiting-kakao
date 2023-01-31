package auth.support;

import auth.annotation.LoginMember;
import nextstep.exception.UnauthenticatedException;
import auth.service.LoginService;
import nextstep.error.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private final LoginService loginService;

    public LoginMemberArgumentResolver(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        try {
            String credential = webRequest.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];
            return loginService.extractMemberDetails(credential);
        } catch (Exception e) {
            throw new UnauthenticatedException(ErrorCode.AUTHENTICATION_FAIL);
        }
    }
}