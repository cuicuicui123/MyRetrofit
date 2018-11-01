package com.cwc.testannotation;

/**
 * @author Cuiweicong 2018/6/15
 */

public interface MyApi {
    @Get("path")
    String sendRequest(@Parameter("a") int a, @Parameter("b") int b);
}
