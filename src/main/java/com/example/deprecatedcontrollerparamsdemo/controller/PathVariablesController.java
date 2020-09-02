package com.example.deprecatedcontrollerparamsdemo.controller;

import com.example.deprecatedcontrollerparamsdemo.filter.PathVariablesServletFilter;
import com.example.deprecatedcontrollerparamsdemo.Utils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/path-variables")
public class PathVariablesController {

    @Value("${app.version}")
    private String version;

    @ApiOperation(value = "first-actual-method", notes = "First endpoint. Uses separated controller method for deprecated endpoint.")
    @GetMapping("/first-actual-method/{id}")
    public String firstActualMethod(
            @PathVariable String id
    ) {
        return "Hello, " + id + "!";
    }

    @ApiOperation(value = "first-deprecated-method", notes = "First deprecated endpoint. Can be hidden or marked with tag, e.g. \"deprecated\"."
            //, tags = "deprecated" // moves endpoint to "deprecated" section
            //, hidden = true // hides endpoint
    )
    @GetMapping("/first-deprecated-method/{id}")
    public String firstDeprecatedMethod(
            @PathVariable String id
    ) {
        Utils.checkVersion(version);
        return firstActualMethod(id);
    }

    @ApiOperation(value = "second-actual-method", notes = "Second endpoint. Uses same controller method with two paths in mapping annotation.")
    @GetMapping({"/second-actual-method/{id}", "/second-deprecated-method/{id}"})
    public String secondActualMethod(
            @PathVariable String id
    ) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();// Utils.checkVersion(version);
        if (request.getServletPath().contains("deprecated")) {
            Utils.checkVersion(version);
        }
        return "Hello, " + id + "!";
    }

    @ApiOperation(value = "third-actual-method", notes = "Third endpoint. Uses XPath in mapping annotation.")
    @GetMapping("/third-{path:(?:actual)|(?:deprecated)}-method/{id}")
    public String thirdActualMethod(
            @PathVariable String id,
            @ApiParam(allowableValues = "actual,deprecated")
            @PathVariable String path
    ) {
        if (path.equals("deprecated")) {
            Utils.checkVersion(version);
        }
        return "Hello, " + id + "!";
    }

    /**
     * Works together with servlet filter.
     * @see PathVariablesServletFilter
     */
    @ApiOperation(value = "fourth-actual-method", notes = "Fourth endpoint. Uses servlet filter to path rewrite when deprecated endpoint called. Try call fourth-deprecated-method instead.")
    @GetMapping("/fourth-actual-method/{id}")
    public String fourthActualMethod(
            @PathVariable String id
    ) {
        return "Hello, " + id + "!";
    }
}
