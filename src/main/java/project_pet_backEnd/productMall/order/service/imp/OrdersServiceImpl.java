package project_pet_backEnd.productMall.order.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project_pet_backEnd.productMall.lineNotify.dto.LineNotifyResponse;
import project_pet_backEnd.productMall.lineNotify.service.LineNotifyService;
import project_pet_backEnd.productMall.order.dao.OrdersDao;
import project_pet_backEnd.productMall.order.dao.OrdersDetailRepository;
import project_pet_backEnd.productMall.order.dao.OrdersRepository;
import project_pet_backEnd.productMall.order.dto.*;
import project_pet_backEnd.productMall.order.dto.response.*;
import project_pet_backEnd.productMall.order.service.OrdersService;
import project_pet_backEnd.productMall.order.vo.OrderDetail;
import project_pet_backEnd.productMall.order.vo.OrderDetailPk;
import project_pet_backEnd.productMall.order.vo.Orders;
import project_pet_backEnd.smtp.EmailService;
import project_pet_backEnd.smtp.dto.EmailResponse;
import project_pet_backEnd.user.dao.UserRepository;
import project_pet_backEnd.user.vo.User;
import project_pet_backEnd.utils.commonDto.ResultResponse;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    OrdersDao ordersDao;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    OrdersDetailRepository ordersDetailRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private LineNotifyService lineNotifyService;


    /**
     * 前台會員新增訂單
     * @param createOrderDTO
     */
    @Override
    @Transactional
    public void createOrders(CreateOrderDTO createOrderDTO, Integer userID) {
        final Orders orders = createOrderDTO.getOrders();
        final List<OrderDetailByCreateDTO> orderDetails = createOrderDTO.getOrderDetailByCreateDTOS();

        if(orders.getOrderAmount() == orders.getTotalAmount()+orders.getOrdFee()-orders.getUserPoint()){
            orders.setUserId(userID);
            ordersRepository.save(orders);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "金額有誤,請重新輸入訂單");
        }

        final var orderListOrderNo = orders.getOrdNo();

        if(orderDetails != null){
            orderDetails.forEach(orderDetail -> {
                final var pdNo = orderDetail.getProNo();
                final var qty = orderDetail.getOrderListQty();
                final var price = orderDetail.getOrderListPrice();

                // OrderDetailPk = OrderMaster ID + Product ID
                OrderDetailPk orderDetailPk = new OrderDetailPk();
                orderDetailPk.setOrdNo(orderListOrderNo);
                orderDetailPk.setPdNo(pdNo);

                OrderDetail orderDetailProduct = new OrderDetail();
                orderDetailProduct.setId(orderDetailPk);
                orderDetailProduct.setQty(qty);
                orderDetailProduct.setPrice(price);
                ordersDetailRepository.save(orderDetailProduct);
            });
        }

        User user = userRepository.findById(userID).orElse(null);
        Integer usePoint = orders.getUserPoint();
        Integer currentPoint = user.getUserPoint();
        String userName = user.getUserName();
        String userEmail = user.getUserEmail();
        Integer newPoint = currentPoint - usePoint;
        if(newPoint >= 0 ){
            user.setUserPoint(newPoint);
            userRepository.save(user);
        }
        String subject = "AllDogCat商成 感謝您本次訂購!";
        String body = "<div style=\"font-size: 16px;\">"
                + "<p>付款狀態為: <strong>尚未付款</strong></p>"
                + "<p>訂單狀態為: <strong>備貨中</strong></p>"
                + "<p>若要付款至訂單詳情，請點擊以下按鈕：</p>"
                + "<a href=\"https://yang-hung-fei.github.io/frontend/pages/mall/order/memberCenterOrders.html\" "
                + "style=\"display: block; text-align: center; text-decoration: none;\">"
                + "<button id=\"checkDetail\" style=\"background-color: #D9AE94; color: white; border: none; border-radius: 5px; "
                + "cursor: pointer; font-size: 16px; padding: 10px 20px;\">訂單詳情</button>"
                + "</a><br><span>※請記得前往<strong>查看訂單詳情付款</strong>，將於付款後3天內出貨！</span>"
                + "</div>";

        sendEmailToCustomer(userEmail, subject, body);

        LineNotifyResponse lineMessage = new LineNotifyResponse();
        String toLineString = "會員名稱:" + userName + "已成功下訂單嚕!!";
        lineMessage.setMessage(toLineString);
        lineNotifyService.notify(lineMessage);
    }

    public  void  sendEmailToCustomer(String to, String subject, String body){
        EmailResponse emailResponse =new EmailResponse(to,subject,body);
        emailService.sendEmail(emailResponse);
    }
    @Override
    public List<OrdersNotCancelDTO> getByUserIdAndOrdStatusNot(Integer userId) {
        Integer ordStatus = 6;
        return ordersRepository.findByOrdStatusNotCancel(userId, ordStatus);
    }

    @Override
    public List<OrderResDTO> getOrderDetailByOrdNo(Integer ordNo) {
        List<FindByOrdNoResDTO> findByOrdNoResDTOS =ordersRepository.findFrontOrderResDtoList(ordNo);
        Map<Integer, OrderResDTO> orderSummaryMap = new HashMap<>();

        for(FindByOrdNoResDTO originalOrder : findByOrdNoResDTOS){
            OrderResDTO orderRes = orderSummaryMap.getOrDefault(originalOrder.getOrdNo(), new OrderResDTO());
            orderRes.setOrdNo(originalOrder.getOrdNo());
            orderRes.setUserName(originalOrder.getUserName());
            orderRes.setUserId(originalOrder.getUserId());
            orderRes.setOrdStatus(originalOrder.getOrdStatus());
            orderRes.setOrdPayStatus(originalOrder.getOrdPayStatus());
            orderRes.setOrdPick(originalOrder.getOrdPick());
            orderRes.setOrdCreate(originalOrder.getOrdCreate());
            orderRes.setOrdFinish(originalOrder.getOrdFinish());
            orderRes.setOrdFee(originalOrder.getOrdFee());
            orderRes.setTotalAmount(originalOrder.getTotalAmount());
            orderRes.setOrderAmount(originalOrder.getOrderAmount());
            orderRes.setRecipientName(originalOrder.getRecipientName());
            orderRes.setRecipientAddress(originalOrder.getRecipientAddress());
            orderRes.setRecipientPh(originalOrder.getRecipientPh());
            orderRes.setEvaluateStatus(originalOrder.getEvaluateStatus());
            orderRes.setUserPoint(originalOrder.getUserPoint());

            OrderDetailResDTO orderDetailResDTO = new OrderDetailResDTO();
            orderDetailResDTO.setPdName(originalOrder.getPdName());
            orderDetailResDTO.setQty(originalOrder.getQty());
            orderDetailResDTO.setPrice(originalOrder.getPrice());

            orderRes.getDetailList().add(orderDetailResDTO);
            orderSummaryMap.put(originalOrder.getOrdNo(), orderRes);
        }

        List<OrderResDTO> orderSummaryList = new ArrayList<>(orderSummaryMap.values());
//        System.out.println(orderSummaryMap.values());
        return orderSummaryList;
    }

    //使用者修改訂單
    @Override
    public String updateOrderStatus(Integer ordNo, Integer ordStatus) {
        Optional<Orders> ordersOptional = ordersRepository.findById(ordNo);
        if(ordersOptional.isPresent()){
            Orders orders = ordersOptional.get();
            orders.setOrdStatus(ordStatus);
            ordersRepository.save(orders);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrderMaster not found with id :" + ordNo);
        }
        return null;
    }

    @Override
    public Integer getUserPoint(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        Integer userPoint = user.getUserPoint();
        return userPoint;
    }

    @Override
    public List<AllOrdersResDTO> getAllOrders(Pageable pageable) {
        List<AllOrdersResDTO> allOrdersResDTOS = ordersRepository.findAllOrdersList(pageable);
        return allOrdersResDTOS;
    }

    @Override
    @Transactional
    public void deleteByOrdNo(Integer ordNo) {

        if(ordersRepository.findByOrdNo(ordNo) == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "操作失敗,無此訂單");
        }

        DeleteOrderDTO deleteOrderDTO = ordersDao.findOrdStatus(ordNo);
        final Integer ordStatus = deleteOrderDTO.getOrdStatus();
        if(ordStatus == 6){
            ordersDao.deleteOrderDetail(ordNo);
            ordersDao.deleteOrder(ordNo);
        }else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN ,"無法刪除該訂單狀態之訂單");
        }
    }


    //管理員修改訂單內容
    @Override
    @Transactional
    public void updateOrderContent(ChangeOrderStatusDTO changeOrderStatusDTO) {
        Orders orders = ordersRepository.findById(changeOrderStatusDTO.getOrdNo()).orElse(null);
        if(changeOrderStatusDTO.getRecipientName() != null){
            orders.setRecipientName(changeOrderStatusDTO.getRecipientName());
        }
        if(changeOrderStatusDTO.getRecipientPh() != null){
            orders.setRecipientPh(changeOrderStatusDTO.getRecipientPh());
        }
        if(changeOrderStatusDTO.getRecipientAddress() != null){
            orders.setRecipientAddress(changeOrderStatusDTO.getRecipientAddress());
        }
        if(changeOrderStatusDTO.getOrdStatus() != null){
            orders.setOrdStatus(changeOrderStatusDTO.getOrdStatus());
        }
        ordersRepository.save(orders);
    }


    //    @Override
//    public void insertOrders(OrdersRes ordersRes) {
//        Orders orders = new Orders();
//        if(ordersRes.getUserId() == null || ordersRes.getUserId() < 0){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無此使用者");
//        }
//        orders.setUserId(ordersRes.getUserId());
//        orders.setOrdStatus(ordersRes.getOrdStatus());
//        orders.setOrdPayStatus(ordersRes.getOrdPayStatus());
//        orders.setOrdPick(ordersRes.getOrdPick());
//        orders.setOrdCreate(ordersRes.getOrdCreate());
//        orders.setOrdFinish(ordersRes.getOrdFinish());
//        orders.setOrdFee(ordersRes.getOrdFee());
//        orders.setTotalAmount(ordersRes.getTotalAmount());
//        orders.setOrderAmount(ordersRes.getOrderAmount());
//        orders.setRecipientName(ordersRes.getRecipientName());
//        orders.setRecipientPh(ordersRes.getRecipientPh());
//        orders.setRecipientAddress(ordersRes.getRecipientAddress());
//        orders.setUserPoint(ordersRes.getUserPoint());
//        ordersRepository.save(orders);
//    }


    @Override
    public void deleteOrdersByOrdNo(Integer ordNo) {
        Orders orders = ordersRepository.findById(ordNo).orElse(null);
        if (ordNo < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請輸入正確訂單編號");
        }else if(orders == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無此訂單,請重新輸入正確訂單編號");
        }else{
            ordersRepository.deleteById(ordNo);
        }

    }

    @Override
    public void updateOrdersByOrdNo(Integer ordNo, OrdersResTestDTO ordersResTestDTO) {
        Orders orders = ordersRepository.findById(ordNo).orElse(null);
        if(orders == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無此訂單,請重新輸入正確訂單編號");
        }
        if(ordNo < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請輸入正確的訂單編號");
        }
        orders.setUserId(ordersResTestDTO.getUserId());
        orders.setOrdStatus(Integer.valueOf(ordersResTestDTO.getOrdStatus()));
        orders.setOrdPayStatus(Integer.valueOf(ordersResTestDTO.getOrdPayStatus()));
        orders.setOrdPick(Integer.valueOf(ordersResTestDTO.getOrdPick()));
        orders.setOrdFee(ordersResTestDTO.getOrdFee());
        orders.setTotalAmount(ordersResTestDTO.getTotalAmount());
        orders.setOrderAmount(ordersResTestDTO.getOrderAmount());
        orders.setRecipientName(ordersResTestDTO.getRecipientName());
        orders.setRecipientPh(ordersResTestDTO.getRecipientPh());
        orders.setRecipientAddress(ordersResTestDTO.getRecipientAddress());
        orders.setUserPoint(ordersResTestDTO.getUserPoint());
        ordersRepository.save(orders);
    }


    @Override
    public OrdersResTestDTO getByOrdNo(Integer ordNo) {
        if(ordNo < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請輸入正確的訂單編號");
        }else{
            return ordersDao.getByOrdNo(ordNo);
        }

    }

    @Override
    public List<Orders> findByUserId(Integer userId) {
        if(userId == null){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請輸入正確會員編號");
        }else if(userId != null){
            return ordersRepository.findByUserId(userId);
        }
        return null;
    }

    @Override
    public List<Orders> selectAll() {
        List<Orders> list = new ArrayList<>();
        list = ordersRepository.findAll();
        return list;
    }

    @Override
    @Transactional
    public ResultResponse<String> apiIdSaveByOrdNo(Integer ordNo, FonPaySaveDTO fonPaySaveDTO) {
        Orders byOrdNo = ordersRepository.findByOrdNo(ordNo);
        if(byOrdNo==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無此訂單,請重新提供正確編號");
        }
        byOrdNo.setPaymentTransactionId(fonPaySaveDTO.getPaymentTransactionId());
        byOrdNo.setPaymentUrl(fonPaySaveDTO.getPaymentUrl());
        ordersRepository.save(byOrdNo);
        ResultResponse<String> rs = new ResultResponse<>();
        rs.setMessage("成功!");
        return rs;
    }
}
