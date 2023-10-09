package project_pet_backEnd.productMall.order.dto;

import lombok.Data;

@Data
public class CallbackData {
    private String action;
    private String paymentTransactionId;
    private String status;
    private Integer totalPrice;
    private String paidDate;
    private String paidConfirmDate;
    private String validation;
    private String creditCardNo;
    private String approveCode;
}
