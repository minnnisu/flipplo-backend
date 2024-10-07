package org.flipplo.flipploService.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
    private String errorCode;
    private String message;

    public static ErrorResponseDto fromException(CustomErrorException e){
        return ErrorResponseDto.builder()
                .errorCode(e.getErrorCode().name())
                .message(e.getMessage())
                .build();
    }

    public static ErrorResponseDto of(String errorCode, String message){
        return ErrorResponseDto.builder()
                .errorCode(errorCode)
                .message(message)
                .build();
    }
}
