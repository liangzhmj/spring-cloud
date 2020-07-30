package com.liangzhmj.cat.mq.action.vo;

/**
 * 抽象动作
 * @author liangzhmj
 *
 */
public abstract class AbstractAction {

	protected String name;
	protected int time = 3;

	/**
	 * 执行动作业务
	 * @return true:执行成功,false:执行失败
	 */
	public abstract boolean doAction();

	/**
	 * 成功的回调方法
	 */
	public abstract void onSuccess();
	/**
	 * 失败的回调方法
	 */
	public abstract void onFail();
	/**
	 * 为doAction准备（例如执行次数减一）
	 */
	public abstract void prepareForAction();
	/**
	 * 执行复位 
	 */
	public abstract void reset();
	/**
	 * 获取执行数 
	 */
	public int getTime(){return time;}


	public abstract boolean isValid();
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
