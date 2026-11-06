package com.example.OpenAISpringAI;

import com.openai.errors.InternalServerException;
import com.openai.errors.OpenAIException;
import com.openai.errors.OpenAIIoException;
import com.openai.errors.RateLimitException;

public class ErrorRules 
{

    public static boolean shouldRetry(OpenAIException e) {

        // Network problem or timeout - may work next time
        if (e instanceof OpenAIIoException) {
            return true;
        }

        // 429 - too many requests, wait and try again
        if (e instanceof RateLimitException) {
            return true;
        }

        // 5xx - problem on OpenAI's side, may work next time
        if (e instanceof InternalServerException) {
            return true;
        }

        // 400, 401, 403, 404 ... - our request is wrong, retrying gives the same error
        return false;
    }
	

}