package com.wap.pay;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wap.pay.utils.HttpUtils;

@WebServlet("/UnifiedCheck")
public class UnifiedCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UnifiedCheck() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		String uuid = request.getParameter("uuid");
		
		String uri = "https://pay.swiftpass.cn/pay/unifiedCheck?uuid="+uuid;
		
		String string = HttpUtils.post(uri, new HashMap<String,String>());
		
		response.getWriter().append(string);
		
	}  

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
