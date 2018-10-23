package com.example.manaadvice.popularmovietmdb.api;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import retrofit2.Response;

import static com.example.manaadvice.popularmovietmdb.utils.Constants.PARSING_ERROR;

/**
 * Common class used by API responses.
 * @param <T>
 */
public class ApiResponse<T> {
    private static final String TAG = "ApiResponse";

    public final int code;
    @Nullable
    public final T body;
    @Nullable
    public final String errorMessage;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        code = response.code();
        if(response.isSuccessful()) {
            body = response.body();
            errorMessage = null;
        } else {
            String message = null;
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody().string();
                } catch (IOException ignored) {
                    Log.d(TAG, PARSING_ERROR);
                }
            }
            if (message == null || message.trim().length() == 0) {
                message = response.message();
            }
            errorMessage = message;
            body = null;
        }
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }
}
