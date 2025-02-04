package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.ProductWarehouse;
import ru.yandex.practicum.repository.ProductWarehouseRepository;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];
    private final ProductWarehouseRepository repository;

    public void createProduct(NewProductInWarehouseRequest request) {
        log.info("Получен запрос на добавление нового продукта на склад: {}", request);

        Optional<ProductWarehouse> existingProduct = repository.findByProductId(request.getProductId());

        if (existingProduct.isPresent()) {
            log.warn("Продукт с ID {} уже есть на складе", request.getProductId());
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт уже добавлен на склад");
        }

        ProductWarehouse product = WarehouseMapper.INSTANCE.newProductInWarehouseRequestToProductWarehouse(request);
        repository.save(product);

        log.info("Продукт с ID {} успешно добавлен на склад", request.getProductId());
    }

    public BookedProductDto checkQuantity(ShoppingCartDto dto) {
        log.info("Проверка наличия товаров на складе для корзины пользователя");

        BookedProductDto bookedProductDto = new BookedProductDto();

        for (UUID productId : dto.getProducts().keySet()) {
            Optional<ProductWarehouse> warehouseProduct = repository.findByProductId(productId);

            if (warehouseProduct.isEmpty()) {
                log.error("Продукт с ID {} не найден на складе", productId);
                throw new NoSpecifiedProductInWarehouseException(HttpStatus.BAD_REQUEST, "Продукт не найден на складе");
            }

            ProductWarehouse product = warehouseProduct.get();
            int requestedQuantity = dto.getProducts().get(productId);

            if (product.getQuantity() < requestedQuantity) {
                log.error("Недостаточное количество товара с ID {} на складе. Запрашиваемое количество: {}, доступное: {}",
                        productId, requestedQuantity, product.getQuantity());
                throw new ProductInShoppingCartLowQuantityInWarehouse(HttpStatus.BAD_REQUEST,
                        "Товар из корзины не находится в требуемом количестве на складе");
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

    public void addProduct(AddProductToWarehouseRequest request) {
        log.info("Добавление товара на склад. ID продукта: {}, Количество: {}", request.getProductId(), request.getQuantity());

        Optional<ProductWarehouse> warehouseProduct = repository.findByProductId(request.getProductId());

        if (warehouseProduct.isEmpty()) {
            log.error("Продукт с ID {} не найден на складе", request.getProductId());
            throw new NoSpecifiedProductInWarehouseException(HttpStatus.BAD_REQUEST, "Продукт не найден на складе");
        }

        ProductWarehouse product = warehouseProduct.get();
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
}
