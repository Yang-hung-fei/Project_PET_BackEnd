package project_pet_backEnd.productMall.order.dto.response;

import lombok.Data;

@Data
public class OrderDetailResDTO {
    private String pdName;
    private Integer qty;
    private Integer price;
    private String paymentTransactionId;
    private String refundNo;
    private String paymentUrl;
    private Integer paymentMethod;

}
