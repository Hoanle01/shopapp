package com.project.shopapp.controller;

import com.github.javafaker.Faker;
import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamsException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.util.StringUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor

public class ProductController {
    private final IProductService productService;
    @PostMapping("")
    public ResponseEntity<?> addProduct(@Validated @RequestBody ProductDTO productDTO,
                                    // @RequestPart("file") List<MultipartFile> files,
                                         BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessage=result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
           Product newProduct= productService.createProduct(productDTO);




            return ResponseEntity.ok(newProduct);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PostMapping(value="uploads/{id}",consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(        @PathVariable("id") Long productId,
                                                  @ModelAttribute("files") List <MultipartFile> files
    ) throws IOException, InvalidParamsException {
        try {
            Product existingProduct=productService.getProductById(productId);
            files=files==null?new ArrayList<MultipartFile>(): files;
            if(files.size()>ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
                return ResponseEntity.badRequest().body("You can only upload maxium 5 images ");
            }
            List<ProductImage> productImages=new ArrayList<>();
            for(MultipartFile file:files) {
                //kiểm tra kích thước file và định mức
                if (file.getSize() == 0) {
                    continue;
                }

                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large Maximum is 10MB");

                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("file must be an image");
                }
                //lưu file và cập nhật thumbnail trong DTO
                String fileName =storeFile(file);//thay thế hàm này với code của bạn để lưu file

                //luư vào đối tượng produict trong db
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(), ProductImageDTO.builder().imageUrl(fileName).build()
                );
                productImages.add(productImage);
            };
            return ResponseEntity.ok().body(productImages);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    };
    private String storeFile(MultipartFile file) throws IOException {
       if(!isImageFile(file)||file.getOriginalFilename()==null){
           throw new IOException("invalid image format");
       }
        String filename= StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        //thêm UUID vào trước tên file đã đảm bảo tên file là duy nhất
        String uniqueFilename= UUID.randomUUID().toString()+"_"+filename;
        //Đường dận đến thư mục mà bạn muốn lưu file
        Path uploadDir= Paths.get("uploads");
        //Kiểm tra và tạo thư mục nếu nó không tồn tại
        if(!Files.exists(uploadDir)){
            Files.createDirectory(uploadDir);
        }
        //đường dẫn đầy đủ tên file

        Path destination=Paths.get(uploadDir.toString(),uniqueFilename);
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;

    }
    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        PageRequest pageRequest=PageRequest.of(page,limit, Sort.by("createAt").descending());
        Page<ProductResponse> productPage=productService.getAllProducts(pageRequest);
        //lấy tổng số trang
        int totalPages=productPage.getTotalPages();
        List<ProductResponse> products=productPage.getContent();

        return ResponseEntity.ok(ProductListResponse.builder()
                        .products(products)
                        .totalPage(totalPages)
                .build());

    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id")
                                                     Long productId){
        try {
            Product existingProduct=productService.getProductById(productId);
            return ResponseEntity.ok(existingProduct);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(String.format("Product with id =%d deleted successfully"));

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    //fake data
    //@PostMapping("/generateFakeProducts")
    private ResponseEntity<String> generateFakeProduct(){
        Faker faker=new Faker();
        for(int i=0;i<100000;i++){
            String productName=faker.commerce().productName();
            if(productService.existByName(productName)){
                continue;
            }
            ProductDTO productDTO=ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10,90000000))
                    .descriptions(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long)faker.number().numberBetween(2,4))
                    .build();
            try {
                productService.createProduct(productDTO);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Product generated");
    }
}
