package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.BookedProductMapper;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.BookedProduct;
import ru.yandex.practicum.model.ProductWarehouse;
import ru.yandex.practicum.repository.BookedProductRepository;
import ru.yandex.practicum.repository.ProductWarehouseRepository;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {
    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];
    private final WarehouseMapper warehouseMapper;
    private final BookedProductMapper bookedProductMapper;
    private final ProductWarehouseRepository repository;
    private final BookedProductRepository bookedProductRepository;

    public void createProduct(NewProductInWarehouseRequest request) {
        log.info("Получен запрос на добавление нового продукта на склад: {}", request);
        if (repository.existsByProductId(request.getProductId())) {
            log.warn("Продукт с ID {} уже есть на складе", request.getProductId());
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт уже добавлен на склад");
        }
        ProductWarehouse product = warehouseMapper.newProductInWarehouseRequestToProductWarehouse(request);
        repository.save(product);
        log.info("Продукт с ID {} успешно добавлен на склад", request.getProductId());
    }

    public BookedProductDto checkQuantity(ShoppingCartDto dto) throws NoSpecifiedProductInWarehouseException, ProductInShoppingCartLowQuantityInWarehouse {
        log.info("Проверка наличия товаров на складе для корзины пользователя");
        BookedProductDto bookedProductDto = new BookedProductDto();
        for (UUID productId : dto.getProducts().keySet()) {
            ProductWarehouse product = repository.findByProductId(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Продукт не найден на складе"));
            int requestedQuantity = dto.getProducts().get(productId);
            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Товар из корзины не находится в требуемом количестве на складе");
            }
            bookedProductDto.setDeliveryVolume(
                    bookedProductDto.getDeliveryVolume() +
                    product.getDimension().getHeight() *
                    product.getDimension().getDepth() *
                    product.getDimension().getWidth()
            );
            bookedProductDto.setDeliveryWeight(
                    bookedProductDto.getDeliveryWeight() + product.getWeight()
            );
            if (product.getFragile()) {
                bookedProductDto.setFragile(true);
            }
            log.debug("Продукт с ID {} прошел проверку наличия", productId);
        }
        log.info("Все товары успешно проверены и зарезервированы");
        return bookedProductDto;
    }

    public void addProduct(AddProductToWarehouseRequest request) throws NoSpecifiedProductInWarehouseException {
        log.info("Добавление товара на склад. ID продукта: {}, Количество: {}", request.getProductId(), request.getQuantity());
        ProductWarehouse product = repository.findByProductId(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Продукт не найден на складе"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        repository.save(product);
        log.info("Количество продукта с ID {} на складе успешно обновлено. Новое количество: {}",
                request.getProductId(), product.getQuantity());
    }
    public AddressDto getAddress() {
        log.info("Запрос на получение адреса склада");
        AddressDto address = AddressDto.builder()
                .city(CURRENT_ADDRESS)
                .country(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .build();
        log.info("Возвращен адрес склада: {}", address);
        return address;
    }
    public void shipped(ShippedToDeliveryRequest request) {
        Optional<BookedProduct> bookedProduct = bookedProductRepository.findByOrderId(request.getOrderId());
        bookedProduct.get().setDeliveryId(request.getDeliveryId());
        bookedProductRepository.save(bookedProduct.get());
    }

    public void returnProducts(Map<UUID, Integer> products) {
        for (UUID key : products.keySet()) {
            Optional<ProductWarehouse> productWarehouse = repository.findByProductId(key);
            if (productWarehouse.isEmpty()) {
                throw new NoSpecifiedProductInWarehouseException("Товар не найден");
            }
            productWarehouse.get().setQuantity(productWarehouse.get().getQuantity() +
                                               products.get(key));
            repository.save(productWarehouse.get());
        }
    }

    public BookedProductDto assembly(AssemblyProductsForOrderRequest request) {
        BookedProduct bookedProduct = new BookedProduct();
        bookedProduct.setOrderId(request.getOrderId());
        bookedProduct.setDeliveryVolume(0);
        bookedProduct.setDeliveryWeight(0);
        for (UUID key : request.getProducts().keySet()) {
            Optional<ProductWarehouse> productWarehouse = repository.findByProductId(key);
            if (productWarehouse.isEmpty()) {
                throw new NoSpecifiedProductInWarehouseException("Товар не найден");
            }
            if (productWarehouse.get().getQuantity() < request.getProducts().get(key)) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Товар не находится в требуемом количестве на складе");
            }
            productWarehouse.get().setQuantity(productWarehouse.get().getQuantity() - request.getProducts().get(key));
            repository.save(productWarehouse.get());
            bookedProduct.setDeliveryWeight(bookedProduct.getDeliveryWeight() + productWarehouse.get().getWeight() * request.getProducts().get(key));
            Double volume = productWarehouse.get().getDimension().getDepth() * productWarehouse.get().getDimension().getHeight() * productWarehouse.get().getDimension().getWidth();
            bookedProduct.setDeliveryVolume(volume);
            if (productWarehouse.get().getFragile()) {
                bookedProduct.setFragile(true);
            }
        }
        return bookedProductMapper.bookedProductToDto(bookedProductRepository.save(bookedProduct));
    }
}