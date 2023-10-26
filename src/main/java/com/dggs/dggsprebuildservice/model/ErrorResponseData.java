package com.dggs.dggsprebuildservice.controller.model;


import lombok.Data;

/**
 * 请求失败的返回
 *
 * @author spot
 * @Date 2018/1/4 22:39
 */
@Data
public class ErrorResponseData extends ResponseData {

    /**
     * 异常的具体类名称
     */
    private String exceptionClazz;

    public ErrorResponseData(String message) {
        super(false, DEFAULT_ERROR_CODE, message, null);
    }

    public ErrorResponseData(String code, String message) {
        super(false, code, message, null);
    }

    public ErrorResponseData(String code, String message, Object object) {
        super(false, code, message, object);
    }
}
