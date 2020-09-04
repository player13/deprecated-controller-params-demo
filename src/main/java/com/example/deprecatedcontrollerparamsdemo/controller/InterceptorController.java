package com.example.deprecatedcontrollerparamsdemo.controller;

import com.example.deprecatedcontrollerparamsdemo.interceptor.SubstituteParams;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/interceptor")
public class InterceptorController {


    @SubstituteParams
    @PostMapping("/method")
    public ResponseBodyDto someActualMethod(@RequestParam String actualParam, @RequestBody RequestBodyDto body) {
        System.out.println("Actual param: " + actualParam);
        System.out.println("Actual body: " + body.getNewParam());
        return new ResponseBodyDto("qwe-asd-zxc");
    }

    @SubstituteParams
    @PostMapping("/method/wrapped")
    public DomainObjectWrapper<ResponseBodyDto> someActualMethodWrapped(@RequestParam String actualParam, @RequestBody RequestBodyDto body) {
        System.out.println("Actual param: " + actualParam);
        System.out.println("Actual body: " + body);
        DomainObjectWrapper<ResponseBodyDto> domainObjectWrapper = new DomainObjectWrapper<>();
        domainObjectWrapper.setObject(new ResponseBodyDto("qwe-asd-zxc"));
        domainObjectWrapper.set("oldParam", "fufufu");
        return domainObjectWrapper;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestBodyDto {
        public String newParam;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseBodyDto {
        public String featureset;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DomainObjectWrapper<T> {

        @JsonUnwrapped
        private T object;

        @JsonIgnore
        private Map<String, Object> map = new HashMap<>();

        @JsonAnyGetter
        public Map<String, Object> get() {
            return map;
        }

        @JsonAnySetter
        public void set(String key, Object value) {
            map.put(key, value);
        }
    }
}
