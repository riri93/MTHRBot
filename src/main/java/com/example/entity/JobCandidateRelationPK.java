package com.example.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class JobCandidateRelationPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idJob;
	private int idCandidate;

	public int getIdJob() {
		return idJob;
	}

	public void setIdJob(int idJob) {
		this.idJob = idJob;
	}

	public int getIdCandidate() {
		return idCandidate;
	}

	public void setIdCandidate(int idCandidate) {
		this.idCandidate = idCandidate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idCandidate;
		result = prime * result + idJob;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobCandidateRelationPK other = (JobCandidateRelationPK) obj;
		if (idCandidate != other.idCandidate)
			return false;
		if (idJob != other.idJob)
			return false;
		return true;
	}

}
