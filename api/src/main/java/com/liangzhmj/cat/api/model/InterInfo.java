package com.liangzhmj.cat.api.model;

import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;


/**
 * 接口的基本配置信息
 * @author liangzhmj
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Log4j2
public class InterInfo {

	private int projectId;
	private String interId;
	private String name;
	private int vesion;
	private String exeClass;
	private boolean valid = false;
	
	public InterInfo(Object[] infos){
		//t1.interId,t1.name,t2.fullpackage,t1.projectId
		try {
			this.interId = StringUtils.getCleanString(infos[0]);
			this.name = StringUtils.getCleanString(infos[1]);
			this.exeClass = StringUtils.getCleanString(infos[2]);
			this.projectId = StringUtils.getCleanInteger(infos[3]);
			valid = true;
		} catch (Exception e) {
			log.error(e.getMessage());
			valid = false;
		}
	}

	public boolean isValid() {
		if(StringUtils.isEmpty(interId) || StringUtils.isEmpty(exeClass)){
			return false;
		}
		return valid;
	}

	
}
