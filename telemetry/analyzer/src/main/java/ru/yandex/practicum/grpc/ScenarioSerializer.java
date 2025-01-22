package ru.yandex.practicum.grpc;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Service
public class ScenarioSerializer {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    /**
     * Конструктор для внедрения HubRouterControllerBlockingStub.
     *
     * @param hubRouterClient Экземпляр HubRouterControllerBlockingStub, предоставленный Spring.
     */
    public ScenarioSerializer(HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    /**
     * Отправляет запрос DeviceActionRequest в HubRouterController.
     *
     * @param request Запрос DeviceActionRequest, который необходимо отправить.
     */
    public void send(DeviceActionRequest request) {
        hubRouterClient.handleDeviceAction(request);
    }
}