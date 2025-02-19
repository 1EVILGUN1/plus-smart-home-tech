package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderState;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final WarehouseClient warehouseClient;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;

    public OrderDto getOrderByUser(String userName) {
        log.info("Поиск заказа для пользователя: {}", userName);
        Order order = repository.findByUserName(userName)
                .orElseThrow(() -> {
                    log.warn("Заказ не найден для пользователя: {}", userName);
                    return new NoOrderFoundException("Заказ не найден");
                });
        log.info("Заказ найден: {}", order.getOrderId());
        return orderMapper.orderToDto(order);
    }

    public OrderDto createOrder(String userName, CreateNewOrderRequest request) {
        log.info("Создание нового заказа для пользователя: {}", userName);

        Order order = new Order();
        order.setUserName(userName);
        order.setProducts(new HashMap<>(request.getShoppingCartDto().getProducts()));
        order.setShoppingCartId(request.getShoppingCartDto().getId());
        order.setAddress(addressMapper.dtoToAddress(request.getAddressDto()));
        order.setState(OrderState.NEW);

        Order savedOrder = repository.save(order);
        log.info("Заказ успешно создан: {}", savedOrder.getOrderId());

        return orderMapper.orderToDto(savedOrder);
    }

    public OrderDto returnOrder(ProductReturnRequest request) {
        log.info("Возврат товаров для заказа: {}", request.getOrderId());

        Order order = findOrderById(request.getOrderId());
        order.setState(OrderState.PRODUCT_RETURNED);
        warehouseClient.returnProducts(order.getProducts());

        log.info("Заказ {} переведен в статус: {}", order.getOrderId(), OrderState.PRODUCT_RETURNED);
        return updateOrder(order);
    }

    public OrderDto paymentOrder(UUID orderId) {
        return updateOrderState(orderId, OrderState.PAID);
    }

    public OrderDto failedPaymentOrder(UUID orderId) {
        return updateOrderState(orderId, OrderState.PAYMENT_FAILED);
    }

    public OrderDto deliveryOrder(UUID orderId) {
        return updateOrderState(orderId, OrderState.DELIVERED);
    }

    public OrderDto failedDeliveryOrder(UUID orderId) {
        return updateOrderState(orderId, OrderState.DELIVERY_FAILED);
    }

    public OrderDto completedOrder(UUID orderId) {
        return updateOrderState(orderId, OrderState.COMPLETED);
    }

    public OrderDto calculateTotalOrder(UUID orderId) {
        return updateOrderState(orderId, OrderState.ON_PAYMENT);
    }

    public OrderDto calculateDeliveryOrder(UUID orderId) {
        return updateOrderState(orderId, OrderState.ON_DELIVERY);
    }

    public OrderDto assemblyOrder(UUID orderId) {
        log.info("Сборка заказа: {}", orderId);

        Order order = findOrderById(orderId);
        AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest();
        request.setProducts(order.getProducts());
        request.setOrderId(orderId);
        warehouseClient.assembly(request);
        order.setState(OrderState.ASSEMBLED);

        log.info("Заказ {} переведен в статус: {}", order.getOrderId(), OrderState.ASSEMBLED);
        return updateOrder(order);
    }

    public OrderDto failedAssemblyOrder(UUID orderId) {
        return updateOrderState(orderId, OrderState.ASSEMBLY_FAILED);
    }

    private Order findOrderById(UUID orderId) {
        log.debug("Поиск заказа по ID: {}", orderId);
        return repository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Заказ не найден: {}", orderId);
                    return new NoOrderFoundException("Заказ не найден");
                });
    }

    private OrderDto updateOrderState(UUID orderId, OrderState state) {
        log.info("Обновление состояния заказа {} → {}", orderId, state);
        Order order = findOrderById(orderId);
        order.setState(state);
        return updateOrder(order);
    }

    private OrderDto updateOrder(Order order) {
        log.debug("Сохранение заказа: {}", order.getOrderId());
        return orderMapper.orderToDto(repository.save(order));
    }
}