package com.project.shopapp.services;

import com.project.shopapp.config.ModelMapperConfig;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.OrderResponse;
import lombok.RequiredArgsConstructor;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapperConfig modelMapper;


    @Override
    public Order getOrder(Long Id) {

        return orderRepository.findById(Id).orElse(null);
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
       //xem user_id có tồn tại hay không
        User user=userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()->new DataNotFoundException("User not found"+orderDTO.getUserId()));
        //convert orderDTO=>Order
        //dùng thư viện model mapper
        //Tạo 1 luồng banrg ánh xạ riêng để kiêm soát anh xạ
        modelMapper.modelMapper().typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper->mapper.skip(Order::setId));
        //cập nhật các trường của đơn hàng từ orderDTO
        Order order=new Order();
        modelMapper.modelMapper().map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        //kiểm tra ngày giao hàng phải >=ngày hôm nay
        LocalDate shippingDate=orderDTO.getShippingDate()==null?LocalDate.now():orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Date must be at least today!");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);
//        //TRẢ về người dùng theo dạng orderRespons
//        modelMapper.modelMapper().typeMap(Order.class,OrderResponse.class);
//        OrderResponse orderResponse=new OrderResponse();
//        modelMapper.modelMapper().map(order,orderResponse);
        return order;
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order=orderRepository.findById(id).orElseThrow(()->
                new DataNotFoundException("Cannot find order with id:"+id));
        User existingUser=userRepository.findById(orderDTO.getUserId()).orElseThrow(()->
                new DataNotFoundException("Cannot find order with id:"+id));
        modelMapper.modelMapper().typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper->mapper.skip(Order::setId));
        //cập nhật các trường của đơn hàng từ OrderDTO
        modelMapper.modelMapper().map(orderDTO,order);
        order.setUser(existingUser);

        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
      Order order=orderRepository.findById(id).orElse(null);
        if(order!=null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
