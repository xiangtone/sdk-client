package com.epplus.view;

/***
 * ֧������
 */
public class PayParams{
	
	
	//�۸� ����
	private int price;
	//��Ʒ����  ����
	private String productName;
	//��Ʒ����  ����
	private String productDesc;
	//cpOrderId  ����
	private String cpOrderId;
	
	
	//��½�û�id 
	private String uid;
	//����Զ��orderid �Զ�����
	private String webOrderid;
	//��Ʒ������ 
	private String productId;
	private int ratio;	//�һ���������ʱ����
	private int buyNum;
	private int coinNum;
	private String serverId;
	private String serverName;
	private String roleId;
	private String roleName;
	private int roleLevel;
	//������ʱû��ʹ��
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
