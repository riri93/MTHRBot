package com.example.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Candidate extends UserInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userLineId;
	@NotEmpty
	private String phone;
	private Date birthday;
	@NotEmpty
	private String jLPT;
	private String nearestStation;
	private String workableTime;
	@NotEmpty
	private String durationInJapan;
	private String memo;
	private String progress;
	@ManyToOne
	@JoinColumn(name = "idAdmin", referencedColumnName = "idUser")
	private Admin admin;

	@OneToOne
	@JoinColumn(name = "idChatLineAdmin", referencedColumnName = "idChatLineAdmin")
	private ChatLineAdmin chatLineAdmin;

	@OneToMany(mappedBy = "candidate")
	private List<JobCandidateRelation> jobCandidateRelations;

	public String getUserLineId() {
		return userLineId;
	}

	public void setUserLineId(String userLineId) {
		this.userLineId = userLineId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getjLPT() {
		return jLPT;
	}

	public void setjLPT(String jLPT) {
		this.jLPT = jLPT;
	}

	public String getNearestStation() {
		return nearestStation;
	}

	public void setNearestStation(String nearestStation) {
		this.nearestStation = nearestStation;
	}

	public String getWorkableTime() {
		return workableTime;
	}

	public void setWorkableTime(String workableTime) {
		this.workableTime = workableTime;
	}

	public String getDurationInJapan() {
		return durationInJapan;
	}

	public void setDurationInJapan(String durationInJapan) {
		this.durationInJapan = durationInJapan;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public List<JobCandidateRelation> getJobCandidateRelations() {
		return jobCandidateRelations;
	}

	public void setJobCandidateRelations(List<JobCandidateRelation> jobCandidateRelations) {
		this.jobCandidateRelations = jobCandidateRelations;
	}

	public ChatLineAdmin getChatLineAdmin() {
		return chatLineAdmin;
	}

	public void setChatLineAdmin(ChatLineAdmin chatLineAdmin) {
		this.chatLineAdmin = chatLineAdmin;
	}

}
