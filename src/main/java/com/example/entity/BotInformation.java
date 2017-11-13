package com.example.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BotInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idBotInformation;
	private String searchCriteria = "address";
	private String addressToSearch;

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

}
