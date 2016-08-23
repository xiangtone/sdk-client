<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>微信安全支付</title>
<meta id="viewport" name="viewport"
	content="width=device-width; initial-scale=1.0; maximum-scale=1; user-scalable=no;" />
<meta name="format-detection" content="telephone=no" />
<link href="css/loading.css" rel="stylesheet">
<link href="css/pop.css" rel="stylesheet">
<script type="text/javascript">
	var xmlHttp;
	function createxmlHttpRequest() {
		if (window.ActiveXObject)
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		else if (window.XMLHttpRequest)
			xmlHttp = new XMLHttpRequest();
	}
	function doRequestUsingGet() { 
		createxmlHttpRequest();
		var queryString = "${https}";
		queryString += "&timestamp=" + new Date().getTime();
		xmlHttp.open("GET", queryString);
		xmlHttp.onreadystatechange = handleStateChange;
		xmlHttp.send(null);
	}
	function handleStateChange() {
		var obj = eval('(' + xmlHttp.responseText + ')');
		if (obj.status == '201') {
			location.href = "${callback_url}";
		}
	}
	function query() {
		doRequestUsingGet();
	}
	function dopay() {
		location.href = "${weixin}";
	}
	window.setInterval(query, "8000");
</script>
</head>
<body>
	<div class="loading_wrap">
		<span class="loading animate"></span>
	</div>
	<div class="pop_wraper" id="alert_box1">
		<div class="pop_outer pop_midder">
			<div class="pop_tip">
				<p class="pop_tip_p4">支付确认</p>
				<p class="pop_tip_p5">1、请在微信内完成支付，支付成功页面自动跳转</p>
				<p class="pop_tip_p5">2、如果您未支付，请点击“去支付”完成支付</p>
				<p class="pop_tip_p5">3、如果您未安装微信6.0.2版本及以上版本客户端，请先安装并登陆微信完成支付</p>
				<p class="pop_tip_p3 border b_top">
					<span class="border b_rgt"><button class="p_btn"
							onclick="history.go(-1)">关闭</button></span> <span><a id="cli"
						class="p_btn cols" style="text-decoration: none"
						href="${weixin}">去支付</a></span>
				</p>
			</div>
		</div>
	</div>
	<script>
		document.getElementById("cli").click();
	</script>
	<script>
		//location.href = "${weixin}";
		//location.href = "paysuccess.jsp";
	</script>
</body>
</html>