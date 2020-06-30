package com.gitee.aspectj.annotation.lang.domain;

import com.gitee.aspectj.enums.OperatingType;

public class OperLog {

	/**操作编号*/
	private Long operId;
	/**操作模块*/
	private String title;
	/**操作类型*/
	private Integer operatingType;
	
	public Long getOperId() {
		return operId;
	}
	public void setOperId(Long operId) {
		this.operId = operId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getOperatingType() {
		return operatingType;
	}
	public void setOperatingType(Integer operatingType) {
		this.operatingType = operatingType;
	}
	
	@Override
	public String toString() {
		return "OperLog [operId=" + operId + ", title=" + title + ", operatingType=" + operatingType + "]";
	}
	
	
	
	
	
	
}
