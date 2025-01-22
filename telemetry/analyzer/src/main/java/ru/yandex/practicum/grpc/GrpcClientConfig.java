package ru.yandex.practicum.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

/**
 * Конфигурационный класс для настройки бинов gRPC-клиента.
 */
@Configuration
public class GrpcClientConfig {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    /**
     * Предоставляет бин для gRPC-клиента HubRouterController.
     *
     * @return Экземпляр HubRouterControllerBlockingStub.
     */
    @Bean
    public HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient() {
        return hubRouterClient;
    }
}