package dev.java.ecommerce.basketservice.Service;


import dev.java.ecommerce.basketservice.client.response.PlatziProductResponse;
import dev.java.ecommerce.basketservice.controller.request.BasketRequest;
import dev.java.ecommerce.basketservice.controller.request.PaymentRequest;
import dev.java.ecommerce.basketservice.entity.Basket;
import dev.java.ecommerce.basketservice.entity.Product;
import dev.java.ecommerce.basketservice.entity.Status;
import dev.java.ecommerce.basketservice.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final ProductService productService;

    public Basket  getBaskeyByid(String id){
        return basketRepository
                .findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Basket Not found"));
    }

    public  Basket  createBasket(BasketRequest basketRequest) {

        basketRepository.findByClientAndStatus(basketRequest.clientId(),Status.OPEN)
                .ifPresent(basket -> {
                    throw new IllegalArgumentException("Ja tem uma basket abertta");
                });

        List<Product> products = getProducts(basketRequest);


        Basket basket = Basket.builder()
                .client(basketRequest.clientId())
                .status(Status.OPEN)
                .products(products)
                .build();
        basket.caculateTotalPrice();
        return basketRepository.save(basket);
    }



    public Basket updateBasket(String basketid, BasketRequest basketRequest) {
            Basket savedBasket = getBaskeyByid(basketid);
        List<Product> products = getProducts(basketRequest);
        savedBasket.setProducts(products);
            savedBasket.caculateTotalPrice();
            return basketRepository.save(savedBasket);
    }

    public Basket payBasket(String basketid, PaymentRequest paymentRequest) {
        Basket savedBasket = getBaskeyByid(basketid);
        savedBasket.setPaymentMethod(paymentRequest.getPaymentMethod());
        savedBasket.setStatus(Status.SOLD);
        return basketRepository.save(savedBasket);
    }

    public void deleteBasket(String basketid) {
        basketRepository.deleteById(basketid);
    }
    private List<Product> getProducts(BasketRequest basketRequest) {
        List<Product> products = new ArrayList<>();
        basketRequest.products().forEach(productRequest -> {
            PlatziProductResponse platziProductResponse = productService.getProductById(productRequest.id());
            products.add(Product.builder()
                    .id(platziProductResponse.id())
                    .title(platziProductResponse.title())
                    .price(platziProductResponse.price())
                    .quantity(productRequest.quantity())
                    .build());
        });
        return products;
    }
}
