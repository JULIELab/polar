package de.julielab.polar.pipeline.webservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class PipelineRunException extends Exception{
    public PipelineRunException() {
    }

    public PipelineRunException(String message) {
        super(message);
    }

    public PipelineRunException(String message, Throwable cause) {
        super(message, cause);
    }

    public PipelineRunException(Throwable cause) {
        super(cause);
    }

    public PipelineRunException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
