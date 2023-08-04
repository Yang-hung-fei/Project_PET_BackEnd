package project_pet_backEnd.user.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project_pet_backEnd.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import project_pet_backEnd.user.dto.oAuth.OAuthRequest;
import project_pet_backEnd.user.dto.oAuth.UserInfoResponse;
import project_pet_backEnd.user.service.OAuthService;
import project_pet_backEnd.user.service.UserService;
import project_pet_backEnd.utils.AllDogCatUtils;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.sql.Date;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private OAuthService oAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest){

        ResultResponse responseResult= userService.localSignIn(userLoginRequest);
        return  ResponseEntity.status(HttpStatus.OK).body(responseResult );
    }
    @PostMapping("/customerSignUp")
    public ResponseEntity<?> localSignUp(@RequestBody  @Valid UserSignUpRequest userSignUpRequest){

        userService.localSignUp(userSignUpRequest);
        return  ResponseEntity.status(HttpStatus.OK).body("註冊成功" );
    }

    @PostMapping("/generateCaptcha")
    public  ResponseEntity<ResultResponse> generateCaptcha(@RequestParam @Email String email){
        ResultResponse rs =userService.generateCaptcha(email);
        return  ResponseEntity.status(HttpStatus.OK).body(rs);
    }
    /**取得個人資訊*/
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@RequestAttribute(name = "userId") Integer userId){
        UserProfileResponse userProfileResponse= userService.getUserProfile(userId);
        return  ResponseEntity.status(HttpStatus.OK).body(userProfileResponse);
    }
    /**修改個人資訊*/
    @PostMapping("/profile")
    public ResponseEntity<ResultResponse> adjustUserProfile(
            @RequestAttribute(name = "userId") Integer userId,
            @RequestParam(required = false) @NotBlank String userName,
            @RequestParam(required = false) @NotBlank  String userNickName,
            @RequestParam(required = false) @Max(1) @Min(0) Integer userGender,
            @RequestParam(required = false) @NotBlank  String userPassword,
            @RequestParam(required = false) @NotBlank  String userAddress,
            @RequestParam(required = false) @NotBlank  String userPhone,
            @RequestParam(required = false) Date userBirthday,
            @RequestParam(required = false) MultipartFile userPic
            ){
        AdjustUserProfileRequest adjustUserProfileRequest=new AdjustUserProfileRequest();
        adjustUserProfileRequest.setUserName(userName);
        adjustUserProfileRequest.setUserNickName(userNickName);
        adjustUserProfileRequest.setUserGender(userGender);
        adjustUserProfileRequest.setUserPassword(userPassword);
        adjustUserProfileRequest.setUserPhone(userPhone);
        adjustUserProfileRequest.setUserAddress(userAddress);
        adjustUserProfileRequest.setUserBirthday(userBirthday);
        adjustUserProfileRequest.setUserPic(AllDogCatUtils.convertMultipartFileToByteArray(userPic));
        userService.adjustUserProfile(userId,adjustUserProfileRequest);
        ResultResponse rs =new ResultResponse();
        rs.setMessage("修改完成");
        return  ResponseEntity.status(200).body(rs);
    }
    /**
     * 第三方登入
     * */
    @PostMapping("/googleLogin")
    public ResponseEntity<ResultResponse> googleLogin(@RequestBody OAuthRequest oAuthRequest){
        ResultResponse rs =oAuthService.oAuthLogin(oAuthRequest);
        return ResponseEntity.ok().body(rs);
    } 
}
