package com.liangzhmj.cat.api.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.quartz.Job;

/**
 * 动态任务抽象类
 * @author liangzhmj
 *
 */
@AllArgsConstructor
@Setter
@Getter
public abstract class SynaJob implements Job {

	protected String id;
	protected String name;
	protected String params;
	protected String cron;
	protected boolean valid;


	
}
