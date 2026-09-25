package com.fudn.product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * TODO 5 — Global Exception Handler.
 *
 * Khi ProductNotFoundException bay ra từ bất kỳ Controller nào,
 * class này bắt nó và tự động:
 *   - Trả HTTP 404 Not Found (thay vì 500 mặc định của Spring)
 *   - Trả message từ exception dưới dạng text (body của response)
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   → Spring tự quét toàn bộ package, không cần khai báo thêm gì.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bắt ProductNotFoundException → trả 404 + message chứa id.
     * Message: "Khong tim thay san pham voi id: <id>"
     */
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleProductNotFoundException(ProductNotFoundException ex) {
        return ex.getMessage();
    }
}
