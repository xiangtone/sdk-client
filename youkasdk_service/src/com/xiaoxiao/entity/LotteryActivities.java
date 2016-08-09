package com.xiaoxiao.entity;
/**
 * 
 * @author zgt
 *
 */
public class LotteryActivities {
	
	private long id;
	private String activeType; //
	private int activeCondition; //
	private int reward; //
	private int probability;//
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getActiveType() {
		return activeType;
	}
	public void setActiveType(String activeType) {
		this.activeType = activeType;
	}
	public int getActiveCondition() {
		return activeCondition;
	}
	public void setActiveCondition(int activeCondition) {
		this.activeCondition = activeCondition;
	}
	public int getReward() {
		return reward;
	}
	public void setReward(int reward) {
		this.reward = reward;
	}
	public int getProbability() {
		return probability;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
	
	
	
	
	
	

}
