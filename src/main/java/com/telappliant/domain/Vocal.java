package com.telappliant.domain;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.telappliant.tvoip.asterisk.DelayedProcessForDelete;

public class Vocal{  

	private Integer id;  
	private Integer organisationId; 
	private int userId;
	private String channel1;
	private String channel2;
	private String caUid;
	private int bridged;
	private String callerIdNum;
	private Date created;
	private Date lastModified;
	@JsonIgnore
	protected AtomicBoolean readyForApiCall = new AtomicBoolean();
	@JsonIgnore
	protected AtomicBoolean readyForDeleteApiCall = new AtomicBoolean();
	@JsonIgnore
	protected DelayedProcessForDelete delayDelete;
	
	public DelayedProcessForDelete getDelayDelete() {
		return delayDelete;
	}

	public void setDelayDelete(DelayedProcessForDelete delayDelete) {
		this.delayDelete = delayDelete;
	}

	//protected Integer deleteId;
	protected String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getChannel1() {
		return channel1;
	}

	public void setChannel1(String channel1) {
		this.channel1 = channel1;
	}

	public String getChannel2() {
		return channel2;
	}

	public void setChannel2(String channel2) {
		this.channel2 = channel2;
	}

	public String getCaUid() {
		return caUid;
	}

	public void setCaUid(String caUid) {
		this.caUid = caUid;
	}

	public int getBridged() {
		return bridged;
	}

	public void setBridged(int bridged) {
		this.bridged = bridged;
	}

	public String getCallerIdNum() {
		return callerIdNum;
	}

	public void setCallerIdNum(String callerIdNum) {
		this.callerIdNum = callerIdNum;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public Integer getOrganisationId() {
		return organisationId;
	}

	public void setOrganisationId(Integer organisationId) {
		this.organisationId = organisationId;
	}
	public AtomicBoolean getReadyForApiCall() {
		return readyForApiCall;
	}

	public boolean isReadyForSetup()
	{
	return readyForApiCall.compareAndSet(false, true);
	}
	
	public AtomicBoolean getReadyForDeleteApiCall() {
		return readyForDeleteApiCall;
	}
	
	public boolean isReadyForDeleteApiCall()
	{
	return readyForDeleteApiCall.compareAndSet(false, true);
	}

	//public Integer getDeleteId() {
	//	return deleteId;
	//}

	//public void setDeleteId(Integer deleteId) {
	//	this.deleteId = deleteId;
	//}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}


