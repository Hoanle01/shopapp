package com.project.shopapp.controller;
import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.services.OrderDetailService;
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
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final OrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO) {
       try {
           OrderDetail newOrderDetail=orderDetailService.createOrderDetail(orderDetailDTO);
           return ResponseEntity.ok(newOrderDetail);

       }catch (Exception e){
           return ResponseEntity.badRequest().body(e.getMessage());

       }

    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@Valid @PathVariable("id") long id) throws Exception {
      OrderDetail orderDetail= orderDetailService.getOrderDetail(id);
        return ResponseEntity.ok("getOrderDetail");
    }
    //lấy ra danh sách các order detail của 1 order nào đó
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetail(@Valid  @PathVariable("id") Long id) {
        List<OrderDetail> orderDetails=orderDetailService.findByOrderId(id);
        return ResponseEntity.ok(orderDetails);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @PathVariable("id") Long id , @RequestBody OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        try {
          OrderDetail orderDetail=  orderDetailService.updateOrderDetail(id,orderDetailDTO);
            return ResponseEntity.ok(orderDetail);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@Valid @PathVariable("id") long id) {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok("deleteOrderDetail");
    }
}
