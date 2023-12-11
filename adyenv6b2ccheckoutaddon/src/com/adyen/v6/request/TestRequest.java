package com.adyen.v6.request;

import java.io.Serializable;

public class TestRequest implements Serializable {
    private String testParam;

    public String getTestParam() {
        return testParam;
    }

    public void setTestParam(String testParam) {
        this.testParam = testParam;
    }
}
