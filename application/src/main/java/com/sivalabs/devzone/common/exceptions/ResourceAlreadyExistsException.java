package com.sivalabs.devzone.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends DevZoneException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
