package com.tsystems.logistics.advice;

import com.tsystems.logistics.exception.HardwareException;
import com.tsystems.logistics.exception.SoftwareException;
import com.tsystems.logistics.exception.TruckNotAvailableException;
import com.tsystems.logistics.exception.OrderValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HardwareException.class)
    public ResponseEntity<ApiError> handleHardwareException(HardwareException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.SERVICE_UNAVAILABLE,
                "A hardware error has occurred: " + ex.getLocalizedMessage(),
                "HARDWARE_ERROR"
        );
        return new ResponseEntity<>(apiError, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(SoftwareException.class)
    public ResponseEntity<ApiError> handleSoftwareException(SoftwareException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "A software error has occurred: " + ex.getLocalizedMessage(),
                "SOFTWARE_ERROR"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TruckNotAvailableException.class)
    public ResponseEntity<ApiError> handleTruckNotAvailableException(TruckNotAvailableException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "The truck is not available: " + ex.getLocalizedMessage(),
                "TRUCK_NOT_AVAILABLE"
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OrderValidationException.class)
    public ResponseEntity<ApiError> handleOrderValidationException(OrderValidationException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The order validation failed: " + ex.getLocalizedMessage(),
                "ORDER_VALIDATION_ERROR"
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // Additional handlers for other exceptions can be added here

    private static class ApiError {
        private HttpStatus status;
        private String message;
        private String error;

        public ApiError(HttpStatus status, String message, String error) {
            this.status = status;
            this.message = message;
            this.error = error;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public void setStatus(HttpStatus status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "ApiError{" +
                    "status=" + status +
                    ", message='" + message + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }

}
