package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class OrderResponse extends BaseResponse {

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("fullname")
    private String fullName;
    private String email;
    @JsonProperty( "phone_number")
    private String phoneNumber;
    private String address;
    private String note;
    @JsonProperty("total_money")
    private Float totalMoney;
    @JsonProperty("order_date")
    private LocalDateTime orderDate;
    @JsonProperty("shipping_method")
    private String status;
    private Float shippingMethod;
    @JsonProperty("shipping_address")
    private Float shippingAddress;
    @JsonProperty("payment_method")
    private Float paymentMethod;
    @JsonProperty("trạcking_number")
    private String trackingNumber;

    @JsonProperty("active")
    private Boolean active;

}
