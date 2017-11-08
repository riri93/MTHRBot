package com.example.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Entity
public class Memo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idMemo;

	private String memoText;
	private Date memoDate;

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "idCandidate", referencedColumnName = "idCandidate"),
			@JoinColumn(name = "idAdmin", referencedColumnName = "idAdmin") })
	CandidateAdminRelation candidateAdminRelation;

	public int getIdMemo() {
		return idMemo;
	}

	public void setIdMemo(int idMemo) {
		this.idMemo = idMemo;
	}

	public String getMemoText() {
		return memoText;
	}

	public void setMemoText(String memoText) {
		this.memoText = memoText;
	}

	public Date getMemoDate() {
		return memoDate;
	}

	public void setMemoDate(Date memoDate) {
		this.memoDate = memoDate;
	}

	public CandidateAdminRelation getCandidateAdminRelation() {
		return candidateAdminRelation;
	}

	public void setCandidateAdminRelation(CandidateAdminRelation candidateAdminRelation) {
		this.candidateAdminRelation = candidateAdminRelation;
	}

}
