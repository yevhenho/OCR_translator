package com.example.yevhenho.git_hub_test.models.net;

public class BaseResponse<T> {
    private int status;
    private String error;

    private T data;

    /**
     * No args constructor for use in serialization
     */
    public BaseResponse() {
    }


    /**
     * @param error
     * @param status
     * @param data
     */

    public BaseResponse(int status, String error, T data) {
        this.status = status;
        this.error = error;
        this.data = data;
    }


    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public T getData() {
        return data;
    }
}
