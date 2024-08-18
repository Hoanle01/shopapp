package com.project.shopapp.services;

import com.project.shopapp.config.ModelMapperConfig;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamsException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory=categoryRepository
        
                .findById(productDTO.getCategoryId())
                .orElseThrow(()->new DataNotFoundException("find not category"));

        Product newProduct=Product.builder()
                .name(productDTO.getName())
                .descriptions(productDTO.getDescriptions())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .category(existingCategory)
                .build();

        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long productId) throws DataNotFoundException {

        return productRepository.findById(productId).orElseThrow(()->new DataNotFoundException("cannot find product with id=" +productId));
    }

    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {

        return productRepository.findAll(pageRequest).map(product->{
            ProductResponse productResponse=ProductResponse.builder()
                    .name(product.getName())
                    .descriptions(product.getDescriptions())
                    .price(product.getPrice())
                    .descriptions(product.getDescriptions())
                    .thumbnail(product.getThumbnail())
                    .categoryId(product.getCategory().getId())

                    .build();
            productResponse.setCreateAt(product.getCreateAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            return productResponse;
        });
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) throws DataNotFoundException {
        Product existingProduct=getProductById(id);
        if(existingProduct!=null){
            Category existingCategory=categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(()->new DataNotFoundException("find not category"+productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setDescriptions(productDTO.getDescriptions());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setThumbnail(productDTO.getThumbnail());
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct=productRepository.findById(id);
       // if(optionalProduct.isPresent()){// kiểm tra nó có hay không
         //   productRepository.delete(optionalProduct.get());
        //}
        optionalProduct.ifPresent(productRepository::delete);//tương đương với câu lênhj trên

    }

    @Override
    public boolean existByName(String name) {

        return productRepository.existsByName(name);
    }
    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamsException {
        Product existingProduct=productRepository
                .findById(productId)
                .orElseThrow(()->new DataNotFoundException("Cannot find product with id:"+productImageDTO.getProductId()));
   ProductImage newProductImage=ProductImage.builder()
           .product(existingProduct)
           .imageUrl(productImageDTO.getImageUrl())
           .build();
   //không cho insert quá 5 ảnh cho 1 sản phẩm
        int size=productImageRepository.findByProductId(productId).size();
        if(size>=5){
            throw new InvalidParamsException("Number of image must be <=5 ");
        }
        return productImageRepository.save(newProductImage);
    }
}
