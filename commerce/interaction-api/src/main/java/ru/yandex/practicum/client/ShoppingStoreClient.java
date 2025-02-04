package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.enums.ProductCategory;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {
    @PutMapping
    public ProductDto create(@RequestBody ProductDto productDto);

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId);

    @PostMapping
    public ProductDto update(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    public Boolean remove(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    public Boolean updateQuantityState(@RequestParam UUID productId, @RequestParam String quantityState);
}
