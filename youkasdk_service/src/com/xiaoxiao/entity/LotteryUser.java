package com.xiaoxiao.entity;
/**
 * @author zgt
 *
 */
public class LotteryUser {
	
	private int id;
	/**
	 */
	private String uid;
	/**
	 */
	private String lottery_activitie;
	
	
	public LotteryUser() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	public LotteryUser(String uid, String lottery_activitie) {
		super();
		this.uid = uid;
		this.lottery_activitie = lottery_activitie;
	}





	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getLottery_activitie() {
		return lottery_activitie;
	}
	public void setLottery_activitie(String lottery_activitie) {
		this.lottery_activitie = lottery_activitie;
	}
	
	
	

}
