package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;

import java.util.List;

public interface IOrderService {
    Order getOrder(Long Id );
    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order updateOrder(Long id,OrderDTO orderDTO) throws DataNotFoundException;
    void deleteOrder(Long id);
    List<Order> findByUserId(Long userId);

}
