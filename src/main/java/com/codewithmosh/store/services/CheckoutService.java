package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.CheckoutResponse;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.PaymentException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor//only final fields will be initialized
@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;


    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request){
        var cart=cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if(cart==null){
            throw new CartNotFoundException();
//            return ResponseEntity.badRequest().body(
//                    new ErrorDto("Invalid cart")
////                    Map.of("error","Cart not found")
//            );
        }
        if(cart.isEmpty()){
            throw new CartEmptyException();
//            return ResponseEntity.badRequest().body(
//                    new ErrorDto("Cart is empty")
////                    Map.of("error","Cart empty")
//            );
        }
        var order= Order.fromCart(cart,authService.getCurrentUser());
        orderRepository.save(order);

        try{
            var session=paymentGateway.createCheckoutSession(order);

            cartService.clearCart(cart.getId());

            return new CheckoutResponse(order.getId(),session.getCheckoutUrl());
        }
        catch (PaymentException ex){
            orderRepository.delete(order);
            throw ex;
        }
    }
}
