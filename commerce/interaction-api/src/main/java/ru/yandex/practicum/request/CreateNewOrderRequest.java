package ru.yandex.practicum.request;


import lombok.Data;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

@Data
public class CreateNewOrderRequest {

    private ShoppingCartDto shoppingCartDto;
    private AddressDto addressDto;
}
