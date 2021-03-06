package com.example.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class ShopCandidateRelation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ShopCandidateRelationPK shopCandidateRelationPK;

	private String progress;

	@Column(columnDefinition = "int default 0")
	private int askInterviewCounter;

	@Column(columnDefinition = "int default 0")
	private int passedInterviewMessageCounter;

	@Column(columnDefinition = "int default 0")
	private int remindInterviewCounter;

	@Column(columnDefinition = "int default 0")
	private int askForReasonCounter;

	@Column(columnDefinition = "boolean default false", nullable = false)
	private boolean confirmedInterview;

	@Temporal(TemporalType.TIMESTAMP)
	private Date interviewDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date askInterviewDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date passedInterviewMessageDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date remindInterviewDate;

	@ManyToOne
	@JoinColumn(name = "idCandidate", referencedColumnName = "idUser", insertable = false, updatable = false)
	@JsonIgnoreProperties({ "shopCandidateRelations, jobCandidateRelations", "shop", "jobs", "staffs" })
	private Candidate candidate;

	@ManyToOne
	@JoinColumn(name = "idShop", referencedColumnName = "idShop", insertable = false, updatable = false)
	@JsonIgnoreProperties({ "shopCandidateRelations, jobCandidateRelations", "candidate", "jobs", "staffs" })
	private Shop shop;

	public ShopCandidateRelationPK getShopCandidateRelationPK() {
		return shopCandidateRelationPK;
	}

	public void setShopCandidateRelationPK(ShopCandidateRelationPK shopCandidateRelationPK) {
		this.shopCandidateRelationPK = shopCandidateRelationPK;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Date getInterviewDate() {
		return interviewDate;
	}

	public void setInterviewDate(Date interviewDate) {
		this.interviewDate = interviewDate;
	}

	public Date getAskInterviewDate() {
		return askInterviewDate;
	}

	public void setAskInterviewDate(Date askInterviewDate) {
		this.askInterviewDate = askInterviewDate;
	}

	public boolean isConfirmedInterview() {
		return confirmedInterview;
	}

	public void setConfirmedInterview(boolean confirmedInterview) {
		this.confirmedInterview = confirmedInterview;
	}

	public Date getPassedInterviewMessageDate() {
		return passedInterviewMessageDate;
	}

	public void setPassedInterviewMessageDate(Date passedInterviewMessageDate) {
		this.passedInterviewMessageDate = passedInterviewMessageDate;
	}

	public Date getRemindInterviewDate() {
		return remindInterviewDate;
	}

	public void setRemindInterviewDate(Date remindInterviewDate) {
		this.remindInterviewDate = remindInterviewDate;
	}

	public int getAskInterviewCounter() {
		return askInterviewCounter;
	}

	public void setAskInterviewCounter(int askInterviewCounter) {
		this.askInterviewCounter = askInterviewCounter;
	}

	public int getPassedInterviewMessageCounter() {
		return passedInterviewMessageCounter;
	}

	public void setPassedInterviewMessageCounter(int passedInterviewMessageCounter) {
		this.passedInterviewMessageCounter = passedInterviewMessageCounter;
	}

	public int getRemindInterviewCounter() {
		return remindInterviewCounter;
	}

	public void setRemindInterviewCounter(int remindInterviewCounter) {
		this.remindInterviewCounter = remindInterviewCounter;
	}

	public int getAskForReasonCounter() {
		return askForReasonCounter;
	}

	public void setAskForReasonCounter(int askForReasonCounter) {
		this.askForReasonCounter = askForReasonCounter;
	}

}
