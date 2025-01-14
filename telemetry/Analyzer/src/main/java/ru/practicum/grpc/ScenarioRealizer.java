package ru.practicum.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Service
public class ScenarioRealizer {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public ScenarioRealizer(@GrpcClient("hub-router")
                            HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void send(DeviceActionRequest request) {
        hubRouterClient.handleDeviceAction(request);
    }
}