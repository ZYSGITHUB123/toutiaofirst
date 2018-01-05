package com.nowcoder.service;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;
    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);              //service里都是一些查询的显示，调用dao，给controller用的
    }
    public int addNews(News news) {
        newsDAO.addNews(news);
        return news.getId();
    }
    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }
public String saveImage(MultipartFile file) throws IOException {
        int dotPos=file.getOriginalFilename().lastIndexOf(".");
        if(dotPos<0){
            return null;
        }
        String fileExt=file.getOriginalFilename().substring(dotPos+1).toLowerCase();   //得到文件筐扩展名
        if(!ToutiaoUtil.isFileAllowed(fileExt)){
            return null;
        }
        String fileName= UUID.randomUUID().toString().replace("-","")+"."+fileExt;
        Files.copy(file.getInputStream(),new File(ToutiaoUtil.IMAGE_DIR+fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.IMAGE_DOMAIN+"image?name"+fileName;      //返回的链接是给前端用的
}
    public int updateCommentCount(int id, int count) {
        return newsDAO.updateCommentCount(id, count);
    }

}
