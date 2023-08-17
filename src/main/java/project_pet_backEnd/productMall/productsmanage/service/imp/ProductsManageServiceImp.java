package project_pet_backEnd.productMall.productsmanage.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project_pet_backEnd.productMall.productsmanage.dao.CreateProductPicRepository;
import project_pet_backEnd.productMall.productsmanage.dao.ProductRepository;
import project_pet_backEnd.productMall.productsmanage.dao.ProductsManageDao;
import project_pet_backEnd.productMall.productsmanage.dto.CreateProductInfoRequest;
import project_pet_backEnd.productMall.productsmanage.service.ProductsManageService;
import project_pet_backEnd.productMall.productsmanage.vo.Product;
import project_pet_backEnd.productMall.productsmanage.vo.ProductPic;
import project_pet_backEnd.utils.commonDto.ResultResponse;

import java.util.ArrayList;
import java.util.List;

@Service     // 1.設條件 2.呼叫方法  throw exception rollback
public class ProductsManageServiceImp implements ProductsManageService {

//    @Autowired
//    private ProductsManageDao productsManageDao;  //新增商品不會用到

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CreateProductPicRepository createProductPicRepository;

    @Transactional
    public Product addProductInfoWithPic(CreateProductInfoRequest createProductInfoRequest, List<ProductPic> pics) {
        // 1.創建商品資訊 2.上傳圖片 3.條件(驗證)

        // 創建商品資訊
        Product product = new Product();
        product.setPdName(createProductInfoRequest.getPdName());
        product.setPdPrice(createProductInfoRequest.getPdPrice());
        product.setPdFormat(createProductInfoRequest.getPdFormat());
        product.setPdStatus(createProductInfoRequest.getPdStatus());
        product.setPdDescription(createProductInfoRequest.getPdDescription());

        productRepository.save(product);

        //上傳商品圖片(需轉換格式)
//        ProductPic productPic = new ProductPic();
        pics = new ArrayList<>();
        for (ProductPic pic : pics) {
//            (pic.getPdPic()).convertMultipartFileToByteArray;
        }

        try {


        } catch (Exception e) {
            throw new RuntimeException();
        }
        ResultResponse rs = new ResultResponse();
        rs.setMessage("新增成功!");
        return product;  //先寫著而已
    }
}
