package com.smartrent.system.client;

import com.smartrent.common.dto.HouseStatsVO;
import com.smartrent.common.dto.TrendVO;
import com.smartrent.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调用 house-service 获取房源/预约统计（服务间直连，绕过网关鉴权）。
 * Nacos 未启用，故写死 url（可在 application.yml 用 house.service.url 覆盖）。
 */
@FeignClient(name = "house-stat-client", url = "${house.service.url:http://localhost:8082}")
public interface HouseStatClient {

    @GetMapping("/house/stats")
    R<HouseStatsVO> stats();

    @GetMapping("/house/trend")
    R<TrendVO> trend(@RequestParam("days") int days);

    @GetMapping("/appointment/count")
    R<Long> appointmentCount();
}
