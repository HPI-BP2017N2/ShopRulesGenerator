package de.hpi.shoprulesgenerator.api;

import de.hpi.shoprulesgenerator.dto.ErrorResponse;
import de.hpi.shoprulesgenerator.exception.ShopRulesDoNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Component
@Slf4j
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ShopRulesDoNotExistException.class})
    protected ResponseEntity<Object> handleShopRulesDoNotExist(Exception e, WebRequest request) {
        log.info(e.getMessage());
        return new ErrorResponse().withError(e).send(HttpStatus.NOT_FOUND);
    }
}
