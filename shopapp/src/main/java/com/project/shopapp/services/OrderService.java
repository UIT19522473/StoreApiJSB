package com.project.shopapp.services;

import com.project.shopapp.configs.UserToLongConverter;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFoundException {

        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + orderDTO.getUserId()));

        // Register the custom converter
        modelMapper.addConverter(new UserToLongConverter());

        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));

        Order order = new Order();
        modelMapper.map(orderDTO, order);

        order.setUserId(existingUser);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);

        Date shippingDate = orderDTO.getShippingDate() == null ? new Date() : orderDTO.getShippingDate();

        if (shippingDate.before(new Date())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);

        orderRepository.save(order);

        return modelMapper.map(order, OrderResponse.class);

    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find order by id: " + id));
    }

    @Override
    public List<Order> findByUserId(Long userId) {

        User existingUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Cannot find user by id: " + userId));

        return orderRepository.findByUserId_Id(userId);
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot updated oder by id: " + id));

        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Cannot get order by user id: " + orderDTO.getUserId()));

        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(orderDTO, existingOrder);

        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if(order!=null){
            order.setActive(false);
            orderRepository.save(order);
        }

    }
}
