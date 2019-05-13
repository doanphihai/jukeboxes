package com.example.jukeboxes.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class InvalidLimitParameterException extends ResponseStatusException
{
    InvalidLimitParameterException()
    {
        super(HttpStatus.BAD_REQUEST, "Limit value cannot be negative");
    }
}
