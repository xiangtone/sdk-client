package com.youka.sdk.entry;
/**
 * 彩票 活动
 * @author zgt
 *
 */
public class LotteryActivities {
	
	private long id;
	private String activeType; //活动类型：首冲first；单笔充值满singleOrderOver
	private int activeCondition; //活动条件：单位分
	private int reward; //奖励数量
	private int probability;//活动概率：单位百分之一
	
	
	
	
	public LotteryActivities(long id, String activeType, int activeCondition,
			int reward, int probability) {
		super();
		this.id = id;
		this.activeType = activeType;
		this.activeCondition = activeCondition;
		this.reward = reward;
		this.probability = probability;
	}
	
	public LotteryActivities() {
		// TODO Auto-generated constructor stub
	}
	
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

	@Override
	public String toString() {
		return "LotteryActivities [id=" + id + ", activeType=" + activeType
				+ ", activeCondition=" + activeCondition + ", reward=" + reward
				+ ", probability=" + probability + "]";
	}
	
	
	
	
	
	

}
