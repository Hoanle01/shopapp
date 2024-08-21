package com.project.shopapp.controller;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.IOrderService;
import com.project.shopapp.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    @PostMapping("")
  public ResponseEntity<?> addOrder(@Validated @RequestBody OrderDTO orderDTO, BindingResult result) {
        try {
            if(result.hasErrors()){
                List<String> errorMessage=   result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
          Order orderResponse= orderService.createOrder(orderDTO);
            return ResponseEntity.ok(orderResponse);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
}
@GetMapping("/user/{user_id}")

public ResponseEntity<?> getOrder(@PathVariable("user_id") long user_id) {
    try {
        List<Order> orders=orderService.findByUserId(user_id);
        return ResponseEntity.ok(orders);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());

    }
}
@GetMapping("/{id}")

    public ResponseEntity<?> getOrders(@PathVariable("id") long orderId) {
        try {
            Order existtingOrder= orderService.getOrder(orderId);
            return ResponseEntity.ok(existtingOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }
@PutMapping("/{id}")
public ResponseEntity<?> updateOrder(@Valid @PathVariable("id") long id, @RequestBody OrderDTO orderDTO) {
    try {
        Order order= orderService.updateOrder(id,orderDTO);
        return ResponseEntity.ok(order);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());

    }
}
@DeleteMapping("/{id}")
public ResponseEntity<?> deleteOrder(@Valid @PathVariable("id") long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.ok("delete successfully");
}
}
