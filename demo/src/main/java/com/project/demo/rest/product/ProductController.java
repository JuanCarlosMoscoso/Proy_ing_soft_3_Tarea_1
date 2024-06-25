package com.project.demo.rest.product;

import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public Product addProduct(@RequestBody Product product) {
        return categoryRepository.findById(product.getCategoryId())
                .map(category -> {
                    product.setCategory(category);
                    return productRepository.save(product);
                })
                .orElseThrow(RuntimeException::new);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    return productRepository.save(existingProduct);
                })
                .orElseGet(() -> {
                    product.setId(id);
                    return productRepository.save(product);
                });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public void deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
    }
}
