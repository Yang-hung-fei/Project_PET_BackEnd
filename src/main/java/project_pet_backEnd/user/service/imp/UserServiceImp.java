package project_pet_backEnd.user.service.imp;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import project_pet_backEnd.smtp.EmailService;
import project_pet_backEnd.smtp.dto.EmailResponse;
import project_pet_backEnd.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project_pet_backEnd.user.dao.UserRepository;
import project_pet_backEnd.user.dto.*;
import project_pet_backEnd.user.service.UserService;
import project_pet_backEnd.user.vo.IdentityProvider;
import project_pet_backEnd.user.vo.User;
import project_pet_backEnd.utils.AllDogCatUtils;
import project_pet_backEnd.utils.UserJwtUtil;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserJwtUtil userJwtUtil;

    public  void  localSignUp(UserSignUpRequest userSignUpRequest){
        if(userDao.getUserByEmail(userSignUpRequest.getUserEmail())!=null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"已經有人註冊此帳號");//確認有無此帳號
        if(!validatedCaptcha(userSignUpRequest.getUserEmail(),userSignUpRequest.getCaptcha()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"驗證碼異常，請從新確認驗證碼");//確認驗證碼
        String encodePwd=bCryptPasswordEncoder.encode(userSignUpRequest.getUserPassword());
        userSignUpRequest.setUserPassword(encodePwd);
        userSignUpRequest.setIdentityProvider(IdentityProvider.Local);
        userDao.localSignUp(userSignUpRequest);
    }

    public ResultResponse localSignIn(UserLoginRequest userLoginRequest){
        User validUser=userDao.getUserByEmail(userLoginRequest.getEmail());
        if(validUser==null || validUser.getIdentityProvider()!=IdentityProvider.Local)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"查無此帳號");//確認有無此帳號 或帳號屬於
        boolean isPasswordMatch  =bCryptPasswordEncoder.matches(userLoginRequest.getPassword(),validUser.getUserPassword());
        if(!isPasswordMatch)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"密碼錯誤");//驗證錯誤
        String jwt=userJwtUtil.createJwt(validUser.getUserId().toString());
        ResultResponse responseResult=new ResultResponse();
        responseResult.setMessage(jwt);
        return  responseResult;

    }

    public boolean  validatedCaptcha(String email,String captcha){
        String redisCapt= redisTemplate.opsForValue().get("MEMBER:"+ email);
        if(redisCapt==null || !redisCapt.equals(captcha))
            return  false;
        return  true;
    }


    public ResultResponse generateCaptcha(String email){
        String authCode=AllDogCatUtils.returnAuthCode();
        String  key ="MEMBER:"+ email;
        redisTemplate.opsForValue().set(key,authCode);
        redisTemplate.expire(key,10, TimeUnit.MINUTES);//十分鐘後過期
        ResultResponse rs=new ResultResponse();
        sendEmail(email,"請確認驗證碼","您的驗證碼為 : <br><p>"+authCode+"</p><br>請於十分鐘內輸入");
        rs.setMessage("generate_success");
        System.out.println(authCode);
        return  rs;
    }

    public  void  sendEmail(String to, String subject, String body){
        EmailResponse emailResponse =new EmailResponse(to,subject,body);
        emailService.sendEmail(emailResponse);
    }

    public UserProfileResponse getUserProfile(Integer userId){

        User user=userDao.getUserById(userId);
        if(user==null)
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"找不到使用者");
        UserProfileResponse userProfileResponse =new UserProfileResponse();
        userProfileResponse.setUserName(user.getUserName());
        userProfileResponse.setUserNickName(user.getUserNickName());
        int gender=user.getUserGender();
        switch (gender){
            case 0:
                userProfileResponse.setUserGender("女性");
                break;
            case 1:
                userProfileResponse.setUserGender("男性");
                break;
            case 2:
                userProfileResponse.setUserGender("尚未設定");
                break;
        }
        userProfileResponse.setUserPhone(user.getUserPhone());
        userProfileResponse.setUserAddress(user.getUserAddress());
        userProfileResponse.setUserBirthday(user.getUserBirthday());
        userProfileResponse.setUserPoint(user.getUserPoint());
        userProfileResponse.setUserPic(AllDogCatUtils.base64Encode(user.getUserPic()));
        userProfileResponse.setIdentityProvider(user.getIdentityProvider());
        userProfileResponse.setUserCreated(AllDogCatUtils.timestampToDateFormat(user.getUserCreated()));
        return  userProfileResponse;
    }

    public   ResultResponse adjustUserProfile(Integer userId,AdjustUserProfileRequest adjustUserProfileRequest){
        User user =userRepository.findById(userId).orElse(null);//先檢查有無此使用者
        if(user==null)
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"無此使用者");
        User adjustUser =new User();
        adjustUser.setUserId(userId);
        adjustUser.setUserName(adjustUserProfileRequest.getUserName()==null?user.getUserName():adjustUserProfileRequest.getUserName());
        adjustUser.setUserNickName(adjustUserProfileRequest.getUserNickName()==null?user.getUserNickName():adjustUserProfileRequest.getUserNickName());
        adjustUser.setUserGender(adjustUserProfileRequest.getUserGender()==null?user.getUserGender():adjustUserProfileRequest.getUserGender());
        adjustUser.setUserEmail(user.getUserEmail());

        String pwd =adjustUserProfileRequest.getUserPassword();
        if(pwd!=null){
            pwd=bCryptPasswordEncoder.encode(pwd);
            adjustUser.setUserPassword(pwd);
        }
        else
            adjustUser.setUserPassword(user.getUserPassword());
        adjustUser.setUserPhone(adjustUserProfileRequest.getUserPhone()==null?user.getUserPhone():adjustUserProfileRequest.getUserPhone());
        adjustUser.setUserAddress(adjustUserProfileRequest.getUserAddress()==null?user.getUserAddress():adjustUserProfileRequest.getUserAddress());
        adjustUser.setUserBirthday(adjustUserProfileRequest.getUserBirthday()==null?user.getUserBirthday():adjustUserProfileRequest.getUserBirthday());
        adjustUser.setUserPoint(user.getUserPoint());
        adjustUser.setUserPic(adjustUserProfileRequest.getUserPic()==null?user.getUserPic():adjustUserProfileRequest.getUserPic());
        adjustUser.setIdentityProvider(user.getIdentityProvider());
        adjustUser.setUserCreated(user.getUserCreated());
        userRepository.save(adjustUser);
        return  new ResultResponse();
    }


}