package project_pet_backEnd.socialMedia.post.post.dao;



import project_pet_backEnd.socialMedia.post.post.dto.req.UpPostRequest;
import project_pet_backEnd.socialMedia.post.post.vo.POST;

import java.util.List;

public interface PostDao {

    /**
     * user發布貼文
     */

    POST create(POST post);


    /**
     * 獲取所有貼文
     */

    List<POST> getAllPosts();


    /**
     * 使用postId查詢貼文
     */

    POST getPostById(int postId);


    /**
     * user修改貼文內容
     */

    POST update(int postId, UpPostRequest upPostRequest);

    /**
     * user刪除貼文
     */

    void delete(int postId);


}