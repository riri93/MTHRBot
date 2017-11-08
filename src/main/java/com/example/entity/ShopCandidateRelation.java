package com.example.entity;

import java.io.Serializable;
import java.util.Date;

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

	@Temporal(TemporalType.TIMESTAMP)
	private Date interviewDate;

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

}