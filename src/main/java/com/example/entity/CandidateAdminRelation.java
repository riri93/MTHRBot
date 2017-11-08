package com.example.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class CandidateAdminRelation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CandidateAdminRelationPK candidateAdminRelationPK;

	@ManyToOne
	@JoinColumn(name = "idCandidate", referencedColumnName = "idUser", insertable = false, updatable = false)
	private Candidate candidate;

	@ManyToOne
	@JoinColumn(name = "idAdmin", referencedColumnName = "idUser", insertable = false, updatable = false)
	private Admin admin;

	@OneToMany(mappedBy = "candidateAdminRelation")
	private List<Memo> memos;

	public CandidateAdminRelationPK getCandidateAdminRelationPK() {
		return candidateAdminRelationPK;
	}

	public void setCandidateAdminRelationPK(CandidateAdminRelationPK candidateAdminRelationPK) {
		this.candidateAdminRelationPK = candidateAdminRelationPK;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public List<Memo> getMemos() {
		return memos;
	}

	public void setMemos(List<Memo> memos) {
		this.memos = memos;
	}

}
