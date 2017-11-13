package com.example.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
	private String startWorkingTime;
	private String finishWorkingTime;

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

	public String getStartWorkingTime() {
		return startWorkingTime;
	}

	public void setStartWorkingTime(String startWorkingTime) {
		this.startWorkingTime = startWorkingTime;
	}

	public String getFinishWorkingTime() {
		return finishWorkingTime;
	}

	public void setFinishWorkingTime(String finishWorkingTime) {
		this.finishWorkingTime = finishWorkingTime;
	}

}
