package com.gelatoflow.gelatoflow_api.controller;

import com.gelatoflow.gelatoflow_api.entity.ProductVariantData;
import com.gelatoflow.gelatoflow_api.service.ProductVariantService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

@RestController
@RequestMapping("/product-variant")
public class ProductVariantController {
    @Autowired
    private ProductVariantService productVariantService;

    private final Logger logger = LogManager.getLogger(ProductVariantController.class);

    @GetMapping("/all")
    public List<ProductVariantData> getAll() {
        return productVariantService.getAll();
    }

    @GetMapping("/all/{productId}")
    public List<ProductVariantData> getByProductId(@PathVariable("productId") long productId) {
        return productVariantService.getByProductId(productId);
    }

    @PostMapping("/{productId}")
    public ProductVariantData addVariant(
            @PathVariable(name="productId") Long productId,
            @RequestParam(name="variantName") String variantName,
            @RequestParam(name="quantity") Long quantity) {
        ProductVariantData variant = productVariantService.addVariant(productId, variantName, quantity);
        return variant;

    }

    @PutMapping("/{variantId}")
    public ProductVariantData updateVariant(
            @PathVariable Long variantId,
            @RequestParam String variantName,
            @RequestParam Long quantity) {
        ProductVariantData variant = productVariantService.updateVariant(variantId, variantName, quantity);
        return variant;
    }

    @DeleteMapping("/delete/{variantId}")
    public void deleteVariant(@PathVariable Long variantId) {
        productVariantService.deleteVariant(variantId);
    }
}
