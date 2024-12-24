package com.spring.dto;

import com.spring.entities.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Long id;
    private Set<CartItem> items;
    private BigDecimal totalAmount;
    private UserDto userDto;
}
