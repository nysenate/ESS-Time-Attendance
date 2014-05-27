package gov.nysenate.seta.controller.rest;

import gov.nysenate.seta.client.response.auth.AuthorizationResponse;
import gov.nysenate.seta.model.auth.AuthorizationStatus;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionCtrl extends BaseRestCtrl
{
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthenticatedException.class)
    public @ResponseBody
    AuthorizationResponse handleUnauthenticatedException(UnauthenticatedException ex) {
        Subject subject = getSubject();
        return new AuthorizationResponse(AuthorizationStatus.UNAUTHENTICATED, subject);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public @ResponseBody
    AuthorizationResponse handleUnauthorizedException(UnauthorizedException ex) {
        Subject subject = getSubject();
        return new AuthorizationResponse(AuthorizationStatus.UNAUTHORIZED, subject);
    }
}
