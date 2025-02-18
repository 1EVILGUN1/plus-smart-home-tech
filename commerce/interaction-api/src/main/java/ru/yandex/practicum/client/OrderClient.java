package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.UUID;

@FeignClient(name = "order-client", path = "/api/v1/order")
public interface OrderClient {
    @GetMapping
    OrderDto getOrderByUser(@RequestParam String userName);

    @PutMapping
    OrderDto createOrder(@RequestParam String userName, CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto returnOrder(@RequestBody ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto paymentOrder(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto failedPaymentOrder(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto deliveryOrder(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto faildeDeliveryOrder(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto completedOrder(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalOrder(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto failedAssemblyOrder(@RequestBody UUID orderId);

}