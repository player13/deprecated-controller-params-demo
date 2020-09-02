package com.example.deprecatedcontrollerparamsdemo.controller;

import com.example.deprecatedcontrollerparamsdemo.filter.RequestParamsServletFilter;
import com.example.deprecatedcontrollerparamsdemo.Utils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/request-params")
public class RequestParamsController {

    @Value("${app.version}")
    private String version;

    @ApiOperation(value = "first-method", notes = "First endpoint. Uses separate controller methods and 'param' property of mapping annotation. Does not works with swagger properly.")
    @GetMapping(value = "/first-method", params = "!deprecated-param")
    public String firstActualMethod(
            @RequestParam(name = "actual-param") String actualParam
    ) {
        return "Hello, " + actualParam + "!";
    }

    @ApiOperation(value = "first-method", notes = "First endpoint. Uses separate controller methods and 'param' property of mapping annotation. Does not works with swagger properly.")
    @GetMapping(value = "/first-method", params = "deprecated-param")
    public String firstDeprecatedMethod(
            @RequestParam(name = "deprecated-param") String deprecatedParam
    ) {
        Utils.checkVersion(version);
        return firstActualMethod(deprecatedParam);
    }

    @ApiOperation(value = "second-method", notes = "Second endpoint. Accepts both params - actual and deprecated. Deprecated param can be hidden from API docs.")
    @GetMapping(value = "/second-method")
    public String secondMethod(
            @RequestParam(name = "actual-param", required = false) String actualParam,
            @ApiParam(
                    name = "deprecated-param"
                    //, hidden = true // hides old param
            )
            @RequestParam(name = "deprecated-param", required = false) String deprecatedParam
    ) {
        if (actualParam == null && deprecatedParam != null) {
            Utils.checkVersion(version);
        }
        return "Hello, " + (actualParam != null ? actualParam : deprecatedParam) + "!";
    }

    /**
     * Works together with servlet filter.
     * @see RequestParamsServletFilter
     */
    @ApiOperation(value = "third-method", notes = "Third endpoint. Uses servlet filter to replace deprecated param with actual. Try to use deprecated-param instead of actual-param.")
    @GetMapping(value = "/third-method")
    public String thirdMethod(
            @RequestParam(name = "actual-param") String actualParam
    ) {
        return "Hello, " + actualParam + "!";
    }
}
