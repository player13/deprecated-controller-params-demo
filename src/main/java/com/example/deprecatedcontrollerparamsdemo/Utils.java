package com.example.deprecatedcontrollerparamsdemo;

public class Utils {

    public static void checkVersion(String version) {
        if (version.equals("v1")) {
            throw new RuntimeException("This endpoint is deprecated");
        }
    }
}
