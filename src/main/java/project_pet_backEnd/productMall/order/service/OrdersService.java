package project_pet_backEnd.productMall.order.service;

import org.springframework.data.domain.Pageable;
import project_pet_backEnd.productMall.order.dto.CallbackData;
import project_pet_backEnd.productMall.order.dto.ChangeOrderStatusDTO;
import project_pet_backEnd.productMall.order.dto.CreateOrderDTO;
import project_pet_backEnd.productMall.order.dto.FonPaySaveDTO;
import project_pet_backEnd.productMall.order.dto.response.AllOrdersResDTO;
import project_pet_backEnd.productMall.order.dto.response.OrderResDTO;
import project_pet_backEnd.productMall.order.dto.response.OrdersNotCancelDTO;
import project_pet_backEnd.productMall.order.dto.response.OrdersResTestDTO;
import project_pet_backEnd.productMall.order.vo.Orders;
import project_pet_backEnd.utils.commonDto.ResultResponse;

import java.util.List;

public interface OrdersService {

    //新增訂單商業邏輯
    public abstract void createOrders(CreateOrderDTO createOrderDTO, Integer userId);

    //查詢該位會員所有未出貨之訂單
    public abstract List<OrdersNotCancelDTO> getByUserIdAndOrdStatusNot(Integer userId);

    //查詢該筆訂單詳情
    public abstract List<OrderResDTO> getOrderDetailByOrdNo(Integer ordNo);

    //修改訂單狀態
    public abstract String updateOrderStatus(Integer ordNo, Integer ordStatus);

    //會員查詢剩餘點數
    public abstract Integer getUserPoint(Integer userId);

    //後臺管理員查詢所有訂單
    public abstract List<AllOrdersResDTO> getAllOrders(Pageable pageable);

    //後臺管理員查詢該筆訂單詳情
    //與前台 "查詢該筆訂單詳情" 共用同個

    //後臺管理員刪除已取消訂單
    public abstract void deleteByOrdNo(Integer ordNo);

    //後臺管理員修改訂單內容
    public abstract void updateOrderContent(ChangeOrderStatusDTO changeOrderStatusDTO);


    //--------------------------------------------------------------------------------------------------
    //刪除訂單商業邏輯--byOrdNo
    public abstract void deleteOrdersByOrdNo(Integer ordNo);

    //修改訂單商業邏輯--byOrdNo
    public abstract void updateOrdersByOrdNo(Integer ordNo, OrdersResTestDTO ordersResTestDTO);


    //查詢訂單商業邏輯--byOrdNo
    public abstract OrdersResTestDTO getByOrdNo(Integer ordNo);

    //查詢訂單商業邏輯--byUserId
    public abstract List<Orders> findByUserId(Integer userId);

    //查詢全部訂單商業邏輯
    public abstract List<Orders> selectAll();


    //--------------------------------------------------------------------------------------------------

    //FonPay 創建金流付款成功後存入交易ID

    public ResultResponse<String> apiIdSaveByOrdNo(Integer ordNo, FonPaySaveDTO fonPaySaveDTO );


    //FonPay callbackUrl
    public String fonPayCallbackModify(CallbackData callbackData);

}
