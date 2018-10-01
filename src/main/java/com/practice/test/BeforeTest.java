package com.practice.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.practice.model.User;
import com.practice.service.UserService;

/**
 * 准备Jmeter测试需要的数据
 *
 */
@Controller
public class BeforeTest {
	@Autowired
	UserService userService;

	/**
	 * 生成用户，并插入数据库
	 * 
	 * @param count
	 * @throws Exception
	 */
	@RequestMapping("/prepareUser")
	@ResponseBody
	public String prepareUser(int count) throws Exception {
		List<User> users = new ArrayList<User>(count);
		// 生成用户
		for (int i = 1; i <= count; i++) {
			User user = new User();
			user.setId(i);
			user.setName("user" + i);
			user.setPassword("admin");
			users.add(user);
		}
		System.out.println("create user");

		// 插入数据库
		userService.insert(users);
		System.out.println("insert to db");

		return "success";
	}

	/**
	 * 用数据库中的所有用户执行登录，并将sessionId存入文件中
	 * @param users
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/prepareLoginUser")
	@ResponseBody
	public String prepareLoginUser(HttpSession session) throws IOException {
		List<User> users = userService.findAllUser();
		
		File file = new File("D:/tokens.txt");
		if(file.exists()) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		
		for(int i=0;i<users.size();i++) {
			User user = users.get(i);
			
			URL url = new URL("http://localhost:8080/prepareLogin");
			HttpURLConnection co = (HttpURLConnection)url.openConnection();
			co.setRequestMethod("POST");
			co.setDoOutput(true);
			OutputStream out = co.getOutputStream();
			String params = "name="+user.getName()+"&password="+user.getPassword();
			out.write(params.getBytes());
			out.flush();
			InputStream inputStream = co.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte buff[] = new byte[1024];
			int len = 0;
			while((len = inputStream.read(buff)) >= 0) {
				bout.write(buff, 0 ,len);
			}
			inputStream.close();
			bout.close();
			String sessionId = new String(bout.toByteArray());
			
			raf.seek(raf.length());
			raf.write(sessionId.getBytes());
			raf.write("\r\n".getBytes());
		}
		raf.close();
		
		return "success";
	}

	@RequestMapping("/prepareLogin")
	@ResponseBody
	public String prepareLogin(HttpServletRequest request,HttpSession session) {
		User paramUser = new User(request.getParameter("name"), request.getParameter("password"));
		User dbUser = userService.login(paramUser);
		if(dbUser != null) {
			//登录成功，将用户放入session
			session.setAttribute("user", dbUser);
		}
		return session.getId();
	}
}
