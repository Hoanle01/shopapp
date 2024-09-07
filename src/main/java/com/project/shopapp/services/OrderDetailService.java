package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
@RequiredArgsConstructor
@Service
public class OrderDetailService implements IOrderDetailService{
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {

        Order order=orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()-> new DataNotFoundException("Order not found"));
        Product product=productRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()-> new DataNotFoundException("Product not found"));
        OrderDetail orderDetail=OrderDetail.builder()
                .order(order)
                .product(product)
                .numberOfProducts(orderDetailDTO.getNumberOfProduct())

                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(@Valid @PathVariable("id")Long id) throws Exception {
        return orderDetailRepository.findById(id).orElseThrow(()->new DataNotFoundException("cannot find OrderDetail with id:"+id));
    }

    @Override
    public OrderDetail updateOrderDetail(@Valid @PathVariable("id") Long id, @PathVariable  OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
       //tìm xem order detail có tồn tại k đã
        OrderDetail existingOrderDetail=orderDetailRepository.findById(id).orElseThrow(()->new DataNotFoundException("cannot find order detail with id: "+id));
       Order existingOrder=orderRepository.findById(orderDetailDTO.getOrderId())
               .orElseThrow(()->new DataNotFoundException("cannot find order with id: "+id));
        Product existingProduct=productRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()-> new DataNotFoundException("Product not found"+orderDetailDTO.getProductId()));
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);
        existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProduct());

        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    public void deleteOrderDetail(Long id) {
    orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
