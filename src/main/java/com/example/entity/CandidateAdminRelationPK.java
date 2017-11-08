package com.example.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CandidateAdminRelationPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int idCandidate;
	private int idAdmin;

	public int getIdCandidate() {
		return idCandidate;
	}

	public void setIdCandidate(int idCandidate) {
		this.idCandidate = idCandidate;
	}

	public int getIdAdmin() {
		return idAdmin;
	}

	public void setIdAdmin(int idAdmin) {
		this.idAdmin = idAdmin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idAdmin;
		result = prime * result + idCandidate;
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
		CandidateAdminRelationPK other = (CandidateAdminRelationPK) obj;
		if (idAdmin != other.idAdmin)
			return false;
		if (idCandidate != other.idCandidate)
			return false;
		return true;
	}

}
