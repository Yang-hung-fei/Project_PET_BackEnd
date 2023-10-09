package project_pet_backEnd.productMall.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
