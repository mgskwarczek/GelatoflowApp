package com.gelatoflow.gelatoflow_api.service;

import com.gelatoflow.gelatoflow_api.entity.ProductVariantData;
import com.gelatoflow.gelatoflow_api.entity.ProductsData;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.repository.ProductRepository;
import com.gelatoflow.gelatoflow_api.repository.ProductVariantRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductVariantService {
    @Autowired
    private AuditLogHeaderService auditLogHeaderService;

    private final Logger logger = LogManager.getLogger(ProductVariantService.class);

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    public List<ProductVariantData> getAll(){
        return productVariantRepository.findAll();
    }

    public List<ProductVariantData> getByProductId(Long productId){
        List<ProductVariantData> variant = productVariantRepository.findByProductId(productId);
        if(variant.isEmpty()){
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND, productId));
        }
        return variant;


    }

    @Transactional
    public ProductVariantData addVariant(Long productId, String variantName, Long quantity){

        ProductsData productsData = productRepository.findById(productId)
                .orElseThrow(()-> throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_NOT_FOUND,productId)));


        ProductVariantData productVariantData = new ProductVariantData();
        productVariantData.setCreationDate(LocalDateTime.now());
        productVariantData.setVariantName(variantName);
        productVariantData.setProduct(productsData);
        productVariantData.setQuantity(quantity);

        return productVariantRepository.save(productVariantData);
    }

    @Transactional
    public ProductVariantData updateVariant(Long variantId, String name, Long quantity){
        ProductVariantData productVariantData = productVariantRepository.findById(variantId)
                .orElseThrow(()-> throwAndLogError(logger, new ApplicationException(ErrorCode.VARIANT_NOT_FOUND,variantId)));

        productVariantData.setModificationDate(LocalDateTime.now());
        productVariantData.setVariantName(name);
        productVariantData.setQuantity(quantity);

        return productVariantRepository.save(productVariantData);
    }

    @Transactional
    public void deleteVariant(Long variantId){
        if(!productVariantRepository.existsById(variantId)){
            throwAndLogError(logger, new ApplicationException(ErrorCode.VARIANT_NOT_FOUND,variantId));
        }
        productVariantRepository.deleteById(variantId);
    }
}
