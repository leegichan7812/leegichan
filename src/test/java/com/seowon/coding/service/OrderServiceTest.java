package com.seowon.coding.service;

import com.seowon.coding.domain.model.Order;
import com.seowon.coding.domain.model.OrderItem;
import com.seowon.coding.domain.model.Product;
import com.seowon.coding.domain.repository.OrderRepository;
import com.seowon.coding.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order1;
    private Order order2;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id(1L)
                .name("Test Product 1")
                .price(BigDecimal.valueOf(100.00))
                .stockQuantity(10)
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Test Product 2")
                .price(BigDecimal.valueOf(200.00))
                .stockQuantity(20)
                .build();

        OrderItem item1 = OrderItem.builder()
                .id(1L)
                .product(product1)
                .quantity(2)
                .price(product1.getPrice())
                .build();

        OrderItem item2 = OrderItem.builder()
                .id(2L)
                .product(product2)
                .quantity(1)
                .price(product2.getPrice())
                .build();

        order1 = Order.builder()
                .id(1L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .status(Order.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.valueOf(400.00))
                .build();
        order1.addItem(item1);
        order1.addItem(item2);

        order2 = Order.builder()
                .id(2L)
                .customerName("Jane Smith")
                .customerEmail("jane@example.com")
                .status(Order.OrderStatus.PROCESSING)
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.valueOf(200.00))
                .build();
        order2.addItem(item2);
    }

    @Test
    void getAllOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        Optional<Order> order = orderService.getOrderById(1L);

        assertTrue(order.isPresent());
        assertEquals("John Doe", order.get().getCustomerName());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void updateOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        Order updated = orderService.updateOrder(1L, order1);

        assertNotNull(updated);
        assertEquals("John Doe", updated.getCustomerName());
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).save(order1);
    }

    @Test
    void deleteOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void placeOrder() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        List<Long> productIds = Arrays.asList(1L, 2L);
        List<Integer> quantities = Arrays.asList(2, 1);

        Order placed = orderService.placeOrder("John Doe", "john@example.com", productIds, quantities);

        assertNotNull(placed);
        assertEquals("John Doe", placed.getCustomerName());
        assertEquals("john@example.com", placed.getCustomerEmail());
        assertEquals(Order.OrderStatus.PENDING, placed.getStatus());
        assertEquals(2, placed.getItems().size());

        verify(productRepository, atLeastOnce()).findById(1L);
        verify(productRepository, atLeastOnce()).findById(2L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void checkoutOrder() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        List<OrderProduct> orderProducts = Arrays.asList(
                new OrderProduct(1L, 2),
                new OrderProduct(2L, 1)
        );

        Order placed = orderService.checkoutOrder("John Doe", "john@example.com", orderProducts, "SALE");

        assertNotNull(placed);
        assertEquals("John Doe", placed.getCustomerName());
        assertEquals("john@example.com", placed.getCustomerEmail());
        assertEquals(Order.OrderStatus.PENDING, placed.getStatus());
        assertEquals(2, placed.getItems().size());

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(2L);
        verify(orderRepository, times(1)).save(any(Order.class));

    }
}