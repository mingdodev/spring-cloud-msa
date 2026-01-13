package com.example.userservice.client;

import com.example.userservice.vo.ResponseOrder;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// configuration 옵션으로 이 클래스만을 위한 configuration을 만들어서 빈 주입도 가능
@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders_ng")
    List<ResponseOrder> getOrders(@PathVariable String userId);
}
