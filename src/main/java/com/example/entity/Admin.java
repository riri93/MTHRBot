package com.example.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Admin extends UserInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "idLineBotAdmin", referencedColumnName = "idLineBotAdmin")
	private LineBotAdmin lineBotAdmin;

	@OneToMany(mappedBy = "admin")
	private List<Candidate> candidates;

	public Admin(Admin admin) {
		super();
	}
	public Admin() {
		super();
	}
	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

	public LineBotAdmin getLineBotAdmin() {
		return lineBotAdmin;
	}

	public void setLineBotAdmin(LineBotAdmin lineBotAdmin) {
		this.lineBotAdmin = lineBotAdmin;
	}

}
