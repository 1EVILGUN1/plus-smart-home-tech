package ru.yandex.practicum.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Configuration
public class GrpcClientConfig {

    @GrpcClient("hub-router")  // Это имя должно соответствовать имени клиента в настройках
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @Bean
    public HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient() {
        return hubRouterClient;
    }
}

