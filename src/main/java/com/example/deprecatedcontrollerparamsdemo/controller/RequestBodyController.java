package com.example.deprecatedcontrollerparamsdemo.controller;

import com.example.deprecatedcontrollerparamsdemo.Utils;
import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/request-body")
public class RequestBodyController {

    @Value("${app.version}")
    private String version;

    @ApiOperation(value = "first-method", notes = "First endpoint. Uses same class for request body with annotation @JsonAlias on renamed field.")
    @PostMapping(value = "/first-method")
    public String firstMethod(
            @RequestBody BodyJsonAliasDto body
    ) {
        // TODO: 02.09.2020 how to check deprecated body properties?
        return "Hello, " + body.getParam() + "!";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BodyJsonAliasDto {
        @JsonAlias("old-param")
        private String param;
    }

    @ApiOperation(value = "second-method", notes = "Second endpoint. Uses actual class for request body which inherited from deprecated superclass. Deprecated field can be hidden.")
    @PostMapping(value = "/second-method")
    public String secondMethod(
            @RequestBody ActualBodyInheritanceDto body
    ) {
        if (body.getParam() == null && body.getOldParam() != null) {
            Utils.checkVersion(version);
        }
        return "Hello, " + (body.getParam() != null ? body.getParam() : body.getOldParam()) + "!";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeprecatedBodyInheritanceDto {
        //@ApiModelProperty(hidden = true)
        private String oldParam;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class ActualBodyInheritanceDto extends DeprecatedBodyInheritanceDto {
        private String param;
    }
}
