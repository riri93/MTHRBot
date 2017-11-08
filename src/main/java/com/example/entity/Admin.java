package com.example.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Admin extends UserInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "idLineBotAdmin", referencedColumnName = "idLineBotAdmin")
	@JsonIgnoreProperties({ "admins" })
	private LineBotAdmin lineBotAdmin;

	@OneToMany(mappedBy = "admin")
	@JsonIgnoreProperties({ "admin","memos","candidate" })
	private List<CandidateAdminRelation> candidateAdminRelations;

	@OneToMany(mappedBy = "admin")
	@JsonIgnoreProperties({ "admin" })
	private List<Notification> notifications;

	public Admin(Admin admin) {
		super();
	}

	public Admin() {
		super();
	}

	public LineBotAdmin getLineBotAdmin() {
		return lineBotAdmin;
	}

	public void setLineBotAdmin(LineBotAdmin lineBotAdmin) {
		this.lineBotAdmin = lineBotAdmin;
	}

	public List<CandidateAdminRelation> getCandidateAdminRelations() {
		return candidateAdminRelations;
	}

	public void setCandidateAdminRelations(List<CandidateAdminRelation> candidateAdminRelations) {
		this.candidateAdminRelations = candidateAdminRelations;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

}
