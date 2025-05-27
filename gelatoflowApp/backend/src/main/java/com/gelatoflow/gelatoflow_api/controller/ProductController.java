package com.gelatoflow.gelatoflow_api.controller;

import com.gelatoflow.gelatoflow_api.dto.product.*;
import com.gelatoflow.gelatoflow_api.entity.ProductTypeData;
import com.gelatoflow.gelatoflow_api.entity.ProductVariantData;
import com.gelatoflow.gelatoflow_api.entity.ProductsData;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.repository.ProductRepository;
import com.gelatoflow.gelatoflow_api.repository.ProductTypeRepository;
import com.gelatoflow.gelatoflow_api.service.ProductService;
import com.gelatoflow.gelatoflow_api.service.ProductVariantService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    private final Logger logger = LogManager.getLogger(ProductController.class);
    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductTypeRepository productTypeRepository;

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable(name="id") Long id) {
        validateId(id);
        ProductsData productsData = productService.getById(id);
        return new ProductDto(productsData);
    }

    public void validateId(Long id) {
        if (id == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "id"));
        }
    }

    @PostMapping("/admin/create")
    public Long createProduct(@RequestBody ProductCreateDto productCreateDto) {
        validateProductCreateDto(productCreateDto);
        logger.info("ProductCreateDto successfully validated.");

        boolean productExits = productRepository.existsByNameAndType(
                productCreateDto.getName(),
                productCreateDto.getTypeId());
        if (productExits) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_NAME_WITH_THIS_TYPE_IS_TAKEN, productCreateDto.getName(),productCreateDto.getTypeId()));
        }

        ProductTypeData productType = productTypeRepository.findById(productCreateDto.getTypeId())
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_TYPE_NOT_FOUND, productCreateDto.getTypeId())));

        ProductsData productsData = ProductCreateDto.toEntity(productCreateDto);
        productsData.setType(productType);
        return productService.createProduct(productsData);
    }

    private void validateProductCreateDto(ProductCreateDto productCreateDto) {
        if (productCreateDto.getId() == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "id"));
        }

        if (productCreateDto.getName() == null || productCreateDto.getName().isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "name"));
        }

        if (productCreateDto.getDescription().length() > 100) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.TOO_LONG_VALUE, "description", productCreateDto.getDescription().length(), 100));
        }
        if (productCreateDto.getTypeId() == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "typeId"));
        }
    }

    @PutMapping("/admin/update")
    @Transactional
    public void updateProduct(@Valid @RequestBody ProductCreateDto productUpdateDto) {
        validateProductCreateDto(productUpdateDto);
        logger.info("UserUpdateDto successfully validated.");

        ProductsData productsData = productService.getById(productUpdateDto.getId());

        productsData.setName(productUpdateDto.getName());
        productsData.setDescription(productUpdateDto.getDescription());
        if (productUpdateDto.getTypeId() != null) {
            ProductTypeData productType = productTypeRepository.findById(productUpdateDto.getTypeId())
                    .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(
                            ErrorCode.PRODUCT_TYPE_NOT_FOUND, productUpdateDto.getTypeId())));
            productsData.setType(productType);
        }

        productService.updateProduct(productsData);
    }

    @DeleteMapping("/delete/{productId}")
    public void deleteProduct(@PathVariable(name="productId") Long productId) {
        productService.deleteProduct(productId);
    }

    @GetMapping("/{id}/details")
    public ProductDetailsDto getProductDetails(@PathVariable(name="id") Long id) {
        return ProductDetailsDto.toDto(productService.getProductDetails(id));
    }

    @GetMapping("/{productId}/variants")
    public List<VariantDto> getProductVariants(@PathVariable(name="productId") Long productId) {
        List<ProductVariantData> variants = productVariantService.getByProductId(productId);

        if (variants.isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND,productId));
        }
        return variants.stream()
                .map(VariantDto::new)
                .toList();
    }

    @PostMapping("/{productId}/variants/create")
    public VariantDto addVariant(@PathVariable(name="productId") Long productId, @RequestBody VariantDto variantDto) {
        ProductVariantData variant = productVariantService.addVariant(
                productId,
                variantDto.getVariantName(),
                variantDto.getQuantity());
        return new VariantDto(variant);
    }

    @PutMapping("/variants/{variantId}")
    public void updateVariant(@PathVariable(name="variantId") Long variantId, @RequestBody ProductVariantData variantData) {
        productVariantService.updateVariant(variantId, variantData.getVariantName(), variantData.getQuantity());
    }

    @DeleteMapping("/delete/{variantId}")
    public void deleteVariant(@PathVariable(name="variantId") Long variantId) {
        productVariantService.deleteVariant(variantId);
    }


    @PostMapping("/types/create")
    public ProductTypeData addProductType(@RequestBody ProductTypeData productTypeData) {
        return productService.addProductType(productTypeData);
    }

    @GetMapping("/allWithVariants")
    public List<ProductWithVariantsDto> getAllProductsWithVariants() {
        return productService.getAllProductsWithVariants();
    }
}
