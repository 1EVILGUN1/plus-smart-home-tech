package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {
    @PutMapping
    public ShoppingCartDto addProducts(@RequestParam String userName, @RequestBody Map<UUID, Integer> dto);

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String userName);

    @DeleteMapping
    public void deleteShoppingCart(@RequestParam String userName);

    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String userName, @RequestBody Map<UUID, Integer> dto);

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantityProducts(@RequestParam String userName, @RequestBody ChangeProductQuantityRequest request);
}
