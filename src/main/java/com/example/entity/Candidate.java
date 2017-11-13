package com.example.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Candidate extends UserInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userLineId;
	private String phone;
	private Date birthday;
	private String jLPT;
	private String nearestStation;
	private String workableTime;
	private String durationInJapan;
	private String memo;
	private String progress;

	private Date registerDate;

	@OneToOne
	@JoinColumn(name = "idChatLineAdmin", referencedColumnName = "idChatLineAdmin")
	private ChatLineAdmin chatLineAdmin;

	@OneToMany(mappedBy = "candidate")
	@JsonIgnoreProperties({ "candidate" })
	private List<JobCandidateRelation> jobCandidateRelations;

	@OneToMany(mappedBy = "candidate")
	@JsonIgnoreProperties({ "candidate" })
	private List<CandidateAdminRelation> candidateAdminRelations;

	@OneToMany(mappedBy = "candidate")
	@JsonIgnoreProperties({ "candidate", "jobCandidateRelations", "candidateAdminRelations" })
	private List<ShopCandidateRelation> shopCandidateRelations;

	@OneToOne
	@JoinColumn(name = "idPersonCharge", referencedColumnName = "idUser")
	@JsonIgnoreProperties({ "lineBotAdmin", "candidateAdminRelations", "notifications" })
	private PersonInCharge personInCharge;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "idBotInformation", referencedColumnName = "idBotInformation")
	@JsonIgnoreProperties("candidate")
	private BotInformation botInformation;

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

	public List<CandidateAdminRelation> getCandidateAdminRelations() {
		return candidateAdminRelations;
	}

	public void setCandidateAdminRelations(List<CandidateAdminRelation> candidateAdminRelations) {
		this.candidateAdminRelations = candidateAdminRelations;
	}

	public PersonInCharge getPersonInCharge() {
		return personInCharge;
	}

	public void setPersonInCharge(PersonInCharge personInCharge) {
		this.personInCharge = personInCharge;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;

	}

	public List<ShopCandidateRelation> getShopCandidateRelations() {
		return shopCandidateRelations;
	}

	public void setShopCandidateRelations(List<ShopCandidateRelation> shopCandidateRelations) {
		this.shopCandidateRelations = shopCandidateRelations;
	}

	public BotInformation getBotInformation() {
		return botInformation;
	}

	public void setBotInformation(BotInformation botInformation) {
		this.botInformation = botInformation;
	}

}
