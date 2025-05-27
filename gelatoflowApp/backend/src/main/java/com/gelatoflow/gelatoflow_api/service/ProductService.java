package com.gelatoflow.gelatoflow_api.service;

import com.gelatoflow.gelatoflow_api.dto.product.ProductWithVariantsDto;
import com.gelatoflow.gelatoflow_api.entity.*;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.repository.ProductRepository;
import com.gelatoflow.gelatoflow_api.repository.ProductTypeRepository;
import com.gelatoflow.gelatoflow_api.repository.ProductVariantRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

@Service
public class ProductService {
    @Autowired
    private AuditLogHeaderService auditLogHeaderService;

    private final Logger logger = LogManager.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Transactional
    protected void saveProduct(ProductsData productsData, ApplicationException exception) {
        try {
            productRepository.save(productsData);
        } catch (ApplicationException e) {
            logger.error("Failed to save this change in ProductData: {}", e.getMessage(), e);
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_CREATE_PRODUCT, e));
        }
    }

    public ProductsData getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_NOT_FOUND, id)));
    }

    public List<ProductsData> getAll() {
        return productRepository.findAll();
    }

    public Set<ProductsData> getByName(String name) {
        return productRepository.findByName(name);
    }


    public Long createProduct(ProductsData productsData) {
        productsData.setCreationDate(LocalDateTime.now());

        try {
            productRepository.save(productsData);
        } catch (Exception e) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_CREATE_PRODUCT));
        }
        logger.info("Product {} successfully created.", productsData.getName());
        return productsData.getId();
    }

    public void updateProduct(ProductsData productsData) {
        if (productRepository.findById(productsData.getId()).isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_NOT_FOUND, productsData.getId()));
        }

        productsData.setModificationDate(LocalDateTime.now());

        try {
            productRepository.save(productsData);
        } catch (Exception e) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_UPDATE_PRODUCT));
        }
        logger.info("Product {} successfully updated.", productsData.getName());
    }

    public void deleteProduct(Long id) {
        ProductsData productsData = getById(id);

        productsData.setDeletionDate(LocalDateTime.now());
        saveProduct(productsData, new ApplicationException(ErrorCode.FAILED_TO_DELETE_PRODUCT, id));

        logger.info("Product with id {} was successfully deleted.", id);
    }

    public ProductsData getProductDetails(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_NOT_FOUND, id)));

    }

    public Set<ProductVariantData> getProductVariants(Long productId) {
        ProductsData product = getById(productId);
        return product.getVariants();
    }

    public ProductTypeData addProductType(ProductTypeData productType) {
        return productTypeRepository.save(productType);
    }

    public List<ProductTypeData> getAllProductTypes() {
        return productTypeRepository.findAll();
    }

    public ProductVariantData addProductVariant(Long productId, ProductVariantData productVariant) {
        ProductsData product = getById(productId);
        productVariant.setProduct(product);
        productVariant.setCreationDate(LocalDateTime.now());
        return productVariantRepository.save(productVariant);
    }

    public ProductVariantData getProductVariantById(Long variantId) {
        return productVariantRepository.findById(variantId)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND, variantId)));
    }

    public List<ProductWithVariantsDto> getAllProductsWithVariants() {
        List<ProductsData> products = productRepository.findAll();

        return products.stream()
                .map(ProductWithVariantsDto::new)
                .toList();

    }
}
