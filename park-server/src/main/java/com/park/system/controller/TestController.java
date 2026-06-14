package com.park.system.controller;

import com.park.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/test")
@Api(tags = "测试接口")
public class TestController {

    @GetMapping("/health")
    @ApiOperation(value = "健康检查", notes = "检查服务是否正常运行")
    public R<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>(4);
        result.put("status", "UP");
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("application", "park-server");
        result.put("version", "1.0.0");

        return R.ok(result);
    }
}
