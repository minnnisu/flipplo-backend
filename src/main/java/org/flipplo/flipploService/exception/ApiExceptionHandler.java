package org.flipplo.flipploService.exception;

import lombok.extern.slf4j.Slf4j;
import org.flipplo.flipploService.config.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ApiExceptionHandler {
    @ExceptionHandler(CustomErrorException.class)
    protected ResponseEntity<ErrorResponseDto> handleCustomErrorException(CustomErrorException e) {
        log.error(e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.fromException(e);
        return new ResponseEntity<>(errorResponseDto, e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        NotValidRequestErrorResponseDto.ErrorDescription errorDescription
                = NotValidRequestErrorResponseDto.ErrorDescription.of(ErrorCode.QueryParamTypeMismatchError.getMessage());
        List<NotValidRequestErrorResponseDto.ErrorDescription> ErrorDescriptions
                = List.of(errorDescription);

        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto
                = NotValidRequestErrorResponseDto.of(ErrorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        NotValidRequestErrorResponseDto.ErrorDescription errorDescription
                = NotValidRequestErrorResponseDto.ErrorDescription.of(ErrorCode.MissingQueryParamError.getMessage());
        List<NotValidRequestErrorResponseDto.ErrorDescription> ErrorDescriptions
                = List.of(errorDescription);

        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto
                = NotValidRequestErrorResponseDto.of(ErrorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.of(
                        ErrorCode.AccessDeniedError.name(),
                        ErrorCode.AccessDeniedError.getMessage()
                );

        return new ResponseEntity<>(errorResponseDto, ErrorCode.AccessDeniedError.getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<NotValidRequestErrorResponseDto> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        List<NotValidRequestErrorResponseDto.ErrorDescription> errorDescriptions =
                fieldErrors.stream().map(NotValidRequestErrorResponseDto.ErrorDescription::of).toList();

        NotValidRequestErrorResponseDto notValidRequestErrorResponseDto =
                NotValidRequestErrorResponseDto.of(e, errorDescriptions);

        return new ResponseEntity<>(notValidRequestErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    protected ResponseEntity<ErrorResponseDto> HandleInternalAuthenticationServiceException(Exception e) {
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.of(
                        ErrorCode.InternalServerError.name(),
                        ErrorCode.InternalServerError.getMessage()
                );

        return new ResponseEntity<>(errorResponseDto, ErrorCode.InternalServerError.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponseDto> HandleGeneralException(Exception e) {
        log.error(e.getMessage());
        ErrorResponseDto errorResponseDto =
                ErrorResponseDto.of(
                        ErrorCode.InternalServerError.name(),
                        ErrorCode.InternalServerError.getMessage()
                );

        return new ResponseEntity<>(errorResponseDto, ErrorCode.InternalServerError.getHttpStatus());
    }


}
