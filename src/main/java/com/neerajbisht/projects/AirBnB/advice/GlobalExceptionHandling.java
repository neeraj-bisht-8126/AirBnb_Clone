package com.neerajbisht.projects.AirBnB.advice;

import com.neerajbisht.projects.AirBnB.exception.ResourceNotFoundException;
import com.neerajbisht.projects.AirBnB.exception.UnAuthorizeException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException exception){
        ApiError apiError = ApiError
                .builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(UnAuthorizeException.class)
    public ResponseEntity<ApiResponse<?>> handleUnAuthorizeException(UnAuthorizeException exception){
        ApiError apiError = ApiError.builder()
                .message(exception.getMessage())
                .status(HttpStatus.FORBIDDEN)
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException exception){
        ApiError apiError = ApiError.builder()
                .message(exception.getLocalizedMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJWTException(JwtException exception){
        ApiError apiError = ApiError.builder()
                .message(exception.getLocalizedMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception){
        ApiError apiError = ApiError.builder()
                .message(exception.getLocalizedMessage())
                .status(HttpStatus.FORBIDDEN)
                .build();
        return buildErrorResponseEntity(apiError);
    }

//    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception exception) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }


    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(
                new ApiResponse<>(apiError),
                    apiError.getStatus());
    }

}
