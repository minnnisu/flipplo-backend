package org.flipplo.flipploService.exception;

import lombok.Getter;
import org.flipplo.flipploService.config.ErrorCode;

@Getter
public class CustomErrorException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomErrorException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
