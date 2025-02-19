package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "delivery")
@Getter
@Setter
public class DeliveryConfig {
    private double baseCost;
    private double fragileMultiplier;
    private double weightMultiplier;
    private double volumeMultiplier;
    private double addressDifferenceMultiplier;
}
