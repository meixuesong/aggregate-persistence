package com.github.meixuesong.product;

import com.github.meixuesong.product.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDO {
    private String id;
    private String name;
    private BigDecimal price =BigDecimal.ZERO;

    public Product toProduct() {
        return new Product(getId(), getName(), getPrice());
    }
}
