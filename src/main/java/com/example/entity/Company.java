package com.example.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Company implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idCompany;

	private String nameCompany;

	private String logoCompany;

	private String description;

	@OneToMany(mappedBy = "company")
	private List<Shop> shops;

	public int getIdCompany() {
		return idCompany;
	}

	public void setIdCompany(int idCompany) {
		this.idCompany = idCompany;
	}

	public String getNameCompany() {
		return nameCompany;
	}

	public void setNameCompany(String nameCompany) {
		this.nameCompany = nameCompany;
	}

	public String getLogoCompany() {
		return logoCompany;
	}

	public void setLogoCompany(String logoCompany) {
		this.logoCompany = logoCompany;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Shop> getShops() {
		return shops;
	}

	public void setShops(List<Shop> shops) {
		this.shops = shops;
	}

}
