package project_pet_backEnd.productMall.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data //生成符合 Java Bean getter setter 無參建構子
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORD_NO")
    private Integer ordNo;

    @Column(name = "USER_ID")
    @NotNull
    private Integer userId;

    @Column(name = "ORD_STATUS", insertable = false)
    private Integer ordStatus;

    @Column(name = "ORD_PAY_STATUS")
    private Integer ordPayStatus;

    @Column(name="ORD_PICK")
    private Integer ordPick;

    @Column(name = "ORD_CREATE", insertable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime ordCreate;

    @Column(name = "ORD_FINISH", insertable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime ordFinish;

    @Column(name = "ORD_FEE")
    private Integer ordFee;

    @Column(name = "TOTAL_AMOUNT")
    private Integer totalAmount;

    @Column(name = "ORDER_AMOUNT")
    @Min(0)
    private Integer orderAmount;

    @Column(name = "RECIPIENT")
    @NotBlank
    private String recipientName;

    @Column(name = "RECIPIENT_ADDRESS")
    @NotBlank
    private String recipientAddress;

    @Column(name = "RECIPIENT_PH")
    @NotBlank
    private String recipientPh;

    @Column(name = "EVALUATE_STATUS", insertable = false)
    private Integer evaluateStatus;

    @Column(name = "USER_POINT")
    @NotNull
    private Integer userPoint;

    @Column(name = "PAYMENT_TRANSACTION_ID")
    private String paymentTransactionId;

    @Column(name = "REFUND_NO")
    private String refundNo;

    @Column(name = "PAYMENT_URL")
    private String paymentUrl;

//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<OrderDetail> detailList;
}
