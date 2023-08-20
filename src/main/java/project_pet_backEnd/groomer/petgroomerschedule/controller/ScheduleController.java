package project_pet_backEnd.groomer.petgroomerschedule.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project_pet_backEnd.groomer.petgroomerschedule.dto.response.GetScheduleRes;
import project_pet_backEnd.groomer.petgroomerschedule.dto.response.ListForScheduleRes;
import project_pet_backEnd.groomer.petgroomerschedule.service.GroomerScheduleService;
import project_pet_backEnd.utils.commonDto.ResultResponse;

import java.util.List;

@Api(tags = "美容師班表功能")
@RestController
@Validated
public class ScheduleController {

    @Autowired
    GroomerScheduleService groomerScheduleService;

    //排班頁面選擇美容師
    @ApiOperation("Man排班頁面表列美容師")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization_M", value = "Manager Access Token", required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("hasAnyAuthority('美容師管理')")
    @GetMapping("/manager/schedulePageGroomer")
    public ResultResponse<List<ListForScheduleRes>> getAllGroomerForSchedule(){
        return groomerScheduleService.getAllGroomerForSchedule();
    }

    //查詢該月排班
    @ApiOperation("Man排班表查詢")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization_M", value = "Manager Access Token", required = true, dataType = "string", paramType = "header")
    })
    @PreAuthorize("hasAnyAuthority('美容師管理')")
    @GetMapping("/manager/schedule")
    public ResultResponse<List<GetScheduleRes>> getMonthScheduleByPgId(
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "pgId") Integer pgId,
            @RequestParam(value = "month") Integer month){

        return groomerScheduleService.getMonthScheduleForMan(year, pgId, month);
    }

}