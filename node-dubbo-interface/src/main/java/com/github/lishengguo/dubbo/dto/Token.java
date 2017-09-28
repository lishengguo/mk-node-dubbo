package com.github.lishengguo.dubbo.dto;

import java.io.Serializable;

public class Token implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8833292070648757079L;
	private Long userId;
	private Long orgId;
	private Long versionId;
	private Long appId;
	  
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Long getVersionId() {
		return versionId;
	}
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}
	public Long getAppId() {
		return appId;
	}
	public void setAppId(Long appId) {
		this.appId = appId; 
	}
	 

}
