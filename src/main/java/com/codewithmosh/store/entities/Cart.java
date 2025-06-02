package com.codewithmosh.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carts", schema = "store_api")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 16)
    private UUID id;



    @Column(name = "date_created",insertable = false, updatable = false)
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    private Set<CartItem> items = new LinkedHashSet<>();

    public BigDecimal getTotalPrice() {
        //Same function
//        BigDecimal totalPrice = BigDecimal.ZERO;
//        for (CartItem item : items) {
//            totalPrice = totalPrice.add(item.getTotalPrice());
//        }

        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CartItem getItem(Long productId){
        return items.stream()
                .filter(item->item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public CartItem addItem(Product product){
        //Can use getItem() defined above
        var cartItem =getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setCart(this);
            items.add(cartItem);
        }
        return cartItem;
    }

}