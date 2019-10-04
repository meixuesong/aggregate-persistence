package com.github.meixuesong.product;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private ProductDOMapper productMapper;

    public ProductRepository(ProductDOMapper productMapper) {
        this.productMapper = productMapper;
    }

    public Product findById(String id) {
        ProductDO productDO = productMapper.selectByPrimaryKey(id);

        return productDO.toProduct();
    }

    public List<Product> findByIds(List<String> prodIds) {
        return productMapper.queryListByIDs(prodIds).stream()
                .map(productDO -> productDO.toProduct())
                .collect(Collectors.toList());
    }

    public Map<String, Product> getProductMapByIds(List<String> prodIds) {
        List<Product> products = findByIds(prodIds);
        return products.stream().collect(Collectors.toMap(Product::getId, p -> p));
    }
}
