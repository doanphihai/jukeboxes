package com.example.jukeboxes.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class InvalidOffsetException extends ResponseStatusException
{
    InvalidOffsetException()
    {
        super(HttpStatus.BAD_REQUEST, "Cannot validate the request's offset");
    }
}
