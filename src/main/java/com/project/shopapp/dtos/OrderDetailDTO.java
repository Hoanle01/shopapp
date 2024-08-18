package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value=1,message = "order's Id must be >0")
    private Long orderId;
    @JsonProperty("product_id")
    @Min(value=1,message = "Product's Id must be >0")

    private Long productId;
    @Min(value=0,message = "order's Id must be >=0")

    private Long price;
    @Min(value=1,message = "order's Id must be >=1")

    @JsonProperty("number_of_products")
    private  int numberOfProduct;
    @JsonProperty("total_money")
    @Min(value=0,message = "order's Id must be >=0")

    private int totalMoney;
    private String color;
}
