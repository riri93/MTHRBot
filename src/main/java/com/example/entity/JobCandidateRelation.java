package com.example.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class JobCandidateRelation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private JobCandidateRelationPK jobCandidateRelationPK;

	private String progress;
	private boolean offered = false;
	private boolean applied = false;
	private boolean matched = false;

	private Date offerDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date appliedDate;

	private Date matchDate;
	private Date postedDate;

	@ManyToOne
	@JoinColumn(name = "idCandidate", referencedColumnName = "idUser", insertable = false, updatable = false)
	@JsonIgnoreProperties({ "jobCandidateRelations", "shopCandidateRelations" })
	private Candidate candidate;

	@ManyToOne
	@JoinColumn(name = "idJob", referencedColumnName = "idJob", insertable = false, updatable = false)
	@JsonIgnoreProperties({ "jobCandidateRelations", "shop", "shopCandidateRelations" })
	private Job job;

	@OneToOne
	@JoinColumn(name = "idChat", referencedColumnName = "idChat")
	private Chat chat;

	public JobCandidateRelationPK getJobCandidateRelationPK() {
		return jobCandidateRelationPK;
	}

	public void setJobCandidateRelationPK(JobCandidateRelationPK jobCandidateRelationPK) {
		this.jobCandidateRelationPK = jobCandidateRelationPK;
	}

	public boolean isOffered() {
		return offered;
	}

	public void setOffered(boolean offered) {
		this.offered = offered;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	public Date getOfferDate() {
		return offerDate;
	}

	public void setOfferDate(Date offerDate) {
		this.offerDate = offerDate;
	}

	public Date getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(Date matchDate) {
		this.matchDate = matchDate;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Date getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

	public Date getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

}
