package com.nowcoder;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.News;
import com.nowcoder.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
	UserDAO userDAO;
	@Autowired
	NewsDAO newsDAO;
	@Test
	public void initData() {
		Random random=new Random();
		for(int i=0;i<11;i++){
			User user=new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("USER%d",i));
			user.setPassword("");
			user.setSalt("");               //model层可以看做是对某个具体用户进行的操作，而dao则是与数据库进行交互
			userDAO.addUser(user);

			News news=new News();
			news.setCommentCount(i);
			Date date=new Date();
			date.setTime(date.getTime()+1000*3600*5*i);
			news.setCreatedDate(date);
			news.setImage(String.format("http://images.nowcoder.com/head/%dm.png",i));
			news.setLikeCount(i+1);
			news.setUserId(i+1);
			news.setTitle(String.format("TITLE{%d}",i));
			news.setLink(String.format("http://www.nowcoder.com/%d.html",i));
			newsDAO.addNews(news);

			user.setPassword("newpassword");
			userDAO.updatePassword(user);
		}
		Assert.assertEquals("newpassword",userDAO.selectById(1).getPassword());    //junit的assert,判断是否等于自己所预测的
		userDAO.deleteById(1);
		Assert.assertNull(userDAO.selectById(1));
	}
}
