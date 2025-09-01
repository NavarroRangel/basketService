package dev.java.ecommerce.basketservice.entity;

import dev.java.ecommerce.basketservice.Service.ProductService;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "basket")
public class Basket {
    @Id
    private String basketId;

    private Long client;

    private BigDecimal totalPrice;

    private List<Product>  products;

    private Status status;

    public void caculateTotalPrice(){
        this.totalPrice = products.stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
