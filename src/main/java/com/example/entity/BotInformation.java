package com.example.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class BotInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idBotInformation;
	private String searchCriteria = "address";
	private String addressToSearch;

	@Temporal(TemporalType.TIMESTAMP)
	private Date askForReasonDate;

	@Temporal(TemporalType.TIME)
	private Date startWorkingTime;

	@Temporal(TemporalType.TIME)
	private Date finishWorkingTime;

	public int getIdBotInformation() {
		return idBotInformation;
	}

	public void setIdBotInformation(int idBotInformation) {
		this.idBotInformation = idBotInformation;
	}

	public String getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public String getAddressToSearch() {
		return addressToSearch;
	}

	public void setAddressToSearch(String addressToSearch) {
		this.addressToSearch = addressToSearch;
	}

	public Date getStartWorkingTime() {
		return startWorkingTime;
	}

	public void setStartWorkingTime(Date startWorkingTime) {
		this.startWorkingTime = startWorkingTime;
	}

	public Date getFinishWorkingTime() {
		return finishWorkingTime;
	}

	public void setFinishWorkingTime(Date finishWorkingTime) {
		this.finishWorkingTime = finishWorkingTime;
	}

	public Date getAskForReasonDate() {
		return askForReasonDate;
	}

	public void setAskForReasonDate(Date askForReasonDate) {
		this.askForReasonDate = askForReasonDate;
	}

}
