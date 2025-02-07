package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PutMapping
    void createProduct(@RequestBody NewProductInWarehouseRequest product);


    @PostMapping("/check")
    BookedProductDto checkQuantity(@RequestBody ShoppingCartDto cart);


    @PostMapping("/add")
    void addProduct(@RequestBody AddProductToWarehouseRequest product);


    @GetMapping("/address")
    AddressDto getAddress();

}
