package com.project.shopapp.services;


import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.response.OrderResponse;

import java.util.List;

public interface IOrderService {

    OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFoundException;

    Order getOrderById(Long id);

    List<Order> findByUserId(Long userId);

    Order updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrder(Long id);
}
