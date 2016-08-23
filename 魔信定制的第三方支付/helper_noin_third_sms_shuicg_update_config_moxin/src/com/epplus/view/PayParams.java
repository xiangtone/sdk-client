package com.epplus.view;

/***
 * 支付参数
 */
public class PayParams{
	
	
	//价格 必填
	private int price;
	//商品名称  必填
	private String productName;
	//商品描述  必填
	private String productDesc;
	//cpOrderId  必填
	private String cpOrderId;
	
	
	//登陆用户id 
	private String uid;
	//网络远程orderid 自动生成
	private String webOrderid;
	//商品订单号 
	private String productId;
	private int ratio;	//兑换比例，暂时无用
	private int buyNum;
	private int coinNum;
	private String serverId;
	private String serverName;
	private String roleId;
	private String roleName;
	private int roleLevel;
	//我们暂时没有使用
	private String payNotifyUrl;
	private String vip;
	private String orderID;
	private String extension;
	
	
	public PayParams() {
	}
	
	
	
	public PayParams(int price, String cpOrderId, String productName,String productDesc) {
		this.price = price;
		this.cpOrderId = cpOrderId;
		this.productName = productName;
		this.productDesc = productDesc;
	}


	public String getCpOrderId() {
		return cpOrderId;
	}


	public void setCpOrderId(String cpOrderId) {
		this.cpOrderId = cpOrderId;
	}


	public String getWebOrderid() {
		return webOrderid;
	}
	public void setWebOrderid(String webOrderid) {
		this.webOrderid = webOrderid;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getBuyNum() {
		return buyNum;
	}
	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}
	public int getCoinNum() {
		return coinNum;
	}
	public void setCoinNum(int coinNum) {
		this.coinNum = coinNum;
	}
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public int getRoleLevel() {
		return roleLevel;
	}
	public void setRoleLevel(int roleLevel) {
		this.roleLevel = roleLevel;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public int getRatio() {
		return ratio;
	}
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	public String getPayNotifyUrl() {
		return payNotifyUrl;
	}
	public void setPayNotifyUrl(String payNotifyUrl) {
		this.payNotifyUrl = payNotifyUrl;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	
	
}
