package com.logreposit.froelingreaderservice.communication.http.common;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ResponseErrorHandlerFactory
{
    public static ResponseErrorHandler createWithoutHttpStatusErrorHandling()
    {
        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler()
        {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException
            {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException
            {
            }
        };

        return responseErrorHandler;
    }
}
