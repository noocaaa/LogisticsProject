package com.tsystems.logistics.advice;

import com.tsystems.logistics.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @ExceptionHandler(CargoAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleCargoAlreadyExistsException(CargoAlreadyExistsException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "Cargo already exists: " + ex.getLocalizedMessage(),
                "CARGO_ALREADY_EXISTS"
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CargoNotFoundException.class)
    public ResponseEntity<ApiError> handleCargoNotFoundException(CargoNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Cargo not found: " + ex.getLocalizedMessage(),
                "CARGO_NOT_FOUND"
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CargoAssignmentException.class)
    public ResponseEntity<ApiError> handleCargoAssignmentException(CargoAssignmentException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Error in cargo assignment: " + ex.getLocalizedMessage(),
                "CARGO_ASSIGNMENT_ERROR"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CityAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleCityAlreadyExistsException(CityAlreadyExistsException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "City already exists: " + ex.getLocalizedMessage(),
                "CITY_ALREADY_EXISTS"
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ApiError> handleCityNotFoundException(CityNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "City not found: " + ex.getLocalizedMessage(),
                "CITY_NOT_FOUND"
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "User not found: " + ex.getLocalizedMessage(),
                "USER_NOT_FOUND"
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DistanceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDistanceAlreadyExistsException(DistanceAlreadyExistsException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "Distance already exists: " + ex.getLocalizedMessage(),
                "DISTANCE_ALREADY_EXISTS"
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DistanceNotFoundException.class)
    public ResponseEntity<ApiError> handleDistanceNotFoundException(DistanceNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Distance not found: " + ex.getLocalizedMessage(),
                "DISTANCE_NOT_FOUND"
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DriverAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDriverAlreadyExistsException(DriverAlreadyExistsException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "Driver already exists: " + ex.getLocalizedMessage(),
                "DRIVER_ALREADY_EXISTS"
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ApiError> handleDriverNotFoundException(DriverNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Driver not found: " + ex.getLocalizedMessage(),
                "DRIVER_NOT_FOUND"
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidDriverStatusException.class)
    public ResponseEntity<ApiError> handleInvalidDriverStatusException(InvalidDriverStatusException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Invalid driver status: " + ex.getLocalizedMessage(),
                "INVALID_DRIVER_STATUS"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DriverAssignmentException.class)
    public ResponseEntity<ApiError> handleDriverAssignmentException(DriverAssignmentException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Driver assignment error: " + ex.getLocalizedMessage(),
                "DRIVER_ASSIGNMENT_ERROR"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DriverOrderAssignmentException.class)
    public ResponseEntity<ApiError> handleDriverOrderAssignmentException(DriverOrderAssignmentException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "Driver assignment error: " + ex.getLocalizedMessage(),
                "DRIVER_ORDER_ASSIGNMENT_ERROR"
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DriverOrderNotFoundException.class)
    public ResponseEntity<ApiError> handleDriverOrderNotFoundException(DriverOrderNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Driversorder not found: " + ex.getLocalizedMessage(),
                "DRIVER_ORDER_NOT_FOUND"
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleOrderAlreadyExistsException(OrderAlreadyExistsException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "Order already exists: " + ex.getLocalizedMessage(),
                "ORDER_ALREADY_EXISTS"
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleOrderNotFoundException(OrderNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Order not found: " + ex.getLocalizedMessage(),
                "ORDER_NOT_FOUND"
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderValidationException.class)
    public ResponseEntity<ApiError> handleOrderValidationException(OrderValidationException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Order validation error: " + ex.getLocalizedMessage(),
                "ORDER_VALIDATION_ERROR"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TruckAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleTruckAlreadyExistsException(TruckAlreadyExistsException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "Truck already exists: " + ex.getLocalizedMessage(),
                "TRUCK_ALREADY_EXISTS"
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TruckNotFoundException.class)
    public ResponseEntity<ApiError> handleTruckNotFoundException(TruckNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Truck not found: " + ex.getLocalizedMessage(),
                "TRUCK_NOT_FOUND"
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TruckAssignmentException.class)
    public ResponseEntity<ApiError> handleTruckAssignmentException(TruckAssignmentException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Truck assignment error: " + ex.getLocalizedMessage(),
                "TRUCK_ASSIGNMENT_ERROR"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WaypointNotFoundException.class)
    public ResponseEntity<ApiError> handleWaypointNotFoundException(WaypointNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Waypoint not found: " + ex.getLocalizedMessage(),
                "WAYPOINT_NOT_FOUND"
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCityForWaypointException.class)
    public ResponseEntity<ApiError> handleInvalidCityForWaypointException(InvalidCityForWaypointException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Invalid city for waypoint: " + ex.getLocalizedMessage(),
                "INVALID_CITY_FOR_WAYPOINT"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCargoForWaypointException.class)
    public ResponseEntity<ApiError> handleInvalidCargoForWaypointException(InvalidCargoForWaypointException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Invalid cargo for waypoint: " + ex.getLocalizedMessage(),
                "INVALID_CARGO_FOR_WAYPOINT"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidWaypointTypeException.class)
    public ResponseEntity<ApiError> handleInvalidWaypointTypeException(InvalidWaypointTypeException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Invalid waypoint type: " + ex.getLocalizedMessage(),
                "INVALID_WAYPOINT_TYPE"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }


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
