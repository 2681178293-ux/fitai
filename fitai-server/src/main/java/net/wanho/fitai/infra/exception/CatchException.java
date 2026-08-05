package net.wanho.fitai.infra.exception;

import io.swagger.v3.oas.annotations.Hidden;
import net.wanho.fitai.infra.result.AjaxResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Hidden
public class CatchException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult handleBody(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return AjaxResult.error(400, msg);
    }

    @ExceptionHandler(CustomException.class)
    public AjaxResult catchCustomException(CustomException e) {
        ExceptionResult result = e.getExceptionResult();
        return AjaxResult.error(result.code(), result.msg());
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult catchException(Exception e) {
        return AjaxResult.error(e.getMessage());
    }
}
