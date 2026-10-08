package com.bmsedge.asset.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @Test
    void returnsNotFoundWhenAssetDoesNotExist() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ServletWebRequest request = new ServletWebRequest(
                new MockHttpServletRequest("GET", "/api/assets/missing/image"));

        var response = handler.handleAssetNotFoundException(
                new AssetNotFoundException("missing"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Asset not found with ID: missing", response.getBody().getMessage());
    }
}