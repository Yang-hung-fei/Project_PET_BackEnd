package project_pet_backEnd.productMall.order.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project_pet_backEnd.productMall.order.dto.CallbackData;
import project_pet_backEnd.productMall.order.dto.FonPaySaveDTO;
import project_pet_backEnd.productMall.order.service.OrdersService;
import project_pet_backEnd.utils.commonDto.ResultResponse;

@Api(tags="前台會員訂單相關功能")
@Validated
@RestController
@RequestMapping("/customer")
public class FonPayController {

    @Autowired
    OrdersService ordersService;

    //FonPay CallBack
    @PostMapping("/fonPayCallback")
    public String fonPayCallback(@RequestBody CallbackData callbackData){
        try {
        System.out.println("1");
        return  ordersService.fonPayCallbackModify(callbackData);
        } catch (Exception e) {
            e.printStackTrace();
            // 处理异常情况
            return "SUCCESS";
        }
    }


}
