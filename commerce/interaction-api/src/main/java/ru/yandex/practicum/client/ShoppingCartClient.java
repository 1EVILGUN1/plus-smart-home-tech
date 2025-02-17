package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {
    @PutMapping
    ShoppingCartDto addProducts(@RequestParam String userName, @RequestBody Map<UUID, Integer> dto);

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String userName);

    @DeleteMapping
    void deleteShoppingCart(@RequestParam String userName);

    @PostMapping("/remove")
    ShoppingCartDto removeProducts(@RequestParam String userName, @RequestBody Map<UUID, Integer> dto);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantityProducts(@RequestParam String userName, @RequestBody ChangeProductQuantityRequest request);
}
