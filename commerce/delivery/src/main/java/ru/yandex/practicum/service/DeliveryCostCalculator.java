package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.config.DeliveryConfig;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.OrderDto;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class DeliveryCostCalculator {
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private final DeliveryConfig deliveryConfig;
    private final WarehouseClient warehouseClient;

    public double calculateCost(OrderDto orderDto) {
        double cost = deliveryConfig.getBaseCost();
        AddressDto address = warehouseClient.getAddress();

        cost += getCityCost(address.getCity());
        cost += orderDto.getDeliveryWeight() * deliveryConfig.getWeightMultiplier();
        cost += orderDto.getDeliveryVolume() * deliveryConfig.getVolumeMultiplier();

        if (orderDto.getFragile()) {
            cost += deliveryConfig.getBaseCost() * deliveryConfig.getFragileMultiplier();
        }

        if (!address.getStreet().equals(getCurrentAddress())) {
            cost += deliveryConfig.getBaseCost() * deliveryConfig.getAddressDifferenceMultiplier();
        }

        return cost;
    }

    private double getCityCost(String city) {
        return switch (city) {
            case "ADDRESS_1" -> deliveryConfig.getBaseCost();
            case "ADDRESS_2" -> deliveryConfig.getBaseCost() * 2;
            default -> 0.0;
        };
    }

    private String getCurrentAddress() {
        return ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];
    }
}
