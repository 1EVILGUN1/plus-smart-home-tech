package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final WarehouseClient warehouseClient;

    public OrderDto getOrderByUser(String userName) {
        Optional<Order> order = repository.findByUserName(userName);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }

        return OrderMapper.INSTANCE.orderToDto(order.get());
    }

    public OrderDto createOrder(String userName, CreateNewOrderRequest request) {
        Order order = new Order();
        order.setUserName(userName);
        Map<UUID, Integer> products = new HashMap<>();
        for (UUID id : request.getShoppingCartDto().getProducts().keySet()) {
            products.put(id, request.getShoppingCartDto().getProducts().get(id));
        }
        order.setProducts(products);
        order.setShoppingCartId(request.getShoppingCartDto().getId());
        order.setAddress(AddressMapper.INSTANCE.dtoToAddress(request.getAddressDto()));
        Order newOrder = repository.save(order);
        order.setState(OrderState.NEW);

        return OrderMapper.INSTANCE.orderToDto(newOrder);
    }

    public OrderDto returnOrder(ProductReturnRequest request) {
        Optional<Order> order = repository.findById(request.getOrderId());
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        order.get().setState(OrderState.PRODUCT_RETURNED);

        warehouseClient.returnProducts(order.get().getProducts());

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }

    public OrderDto paymentOrder(UUID orderId) {
        Optional<Order> order = repository.findById(orderId);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        order.get().setState(OrderState.PAID);

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }

    public OrderDto failedPaymentOrder(UUID orderId) {
        Optional<Order> order = repository.findById(orderId);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        order.get().setState(OrderState.PAYMENT_FAILED);

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }

    public OrderDto deliveryOrder(UUID orderId) {
        Optional<Order> order = repository.findById(orderId);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        order.get().setState(OrderState.DELIVERED);

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }

    public OrderDto faildeDeliveryOrder(UUID orderId) {
        Optional<Order> order = repository.findById(orderId);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        order.get().setState(OrderState.DELIVERY_FAILED);

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }

    public OrderDto completedOrder(UUID orderId) {
        Optional<Order> order = repository.findById(orderId);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        order.get().setState(OrderState.COMPLETED);

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }

    public OrderDto calculateTotalOrder(UUID orderId) {
        Optional<Order> order = repository.findById(orderId);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        order.get().setState(OrderState.ON_PAYMENT);

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }

    public OrderDto calculateDeliveryOrder(UUID orderId) {
        Optional<Order> order = repository.findById(orderId);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        order.get().setState(OrderState.ON_DELIVERY);

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }

    public OrderDto assemblyOrder(UUID orderId) {
        Optional<Order> order = repository.findById(orderId);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest();
        request.setProducts(order.get().getProducts());
        request.setOrderId(orderId);
        warehouseClient.assembly(request);
        order.get().setState(OrderState.ASSEMBLED);

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }

    public OrderDto failedAssemblyOrder(UUID orderId) {
        Optional<Order> order = repository.findById(orderId);
        if (order.isEmpty()) {
            throw new NoOrderFoundException(HttpStatus.NOT_FOUND, "Заказ не найден");
        }
        order.get().setState(OrderState.ASSEMBLY_FAILED);

        return OrderMapper.INSTANCE.orderToDto(repository.save(order.get()));
    }
}