package com.example.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Shop implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idShop;

	private String nameShop;
	private String addressShop;
	private String descriptionShop;
	private String nearestStation;
	private String category;
	private String openTime;
	private String logoShop;
	private String userIdLine;
	private String channelToken;

	@ManyToOne
	@JoinColumn(name = "idCompany", referencedColumnName = "idCompany")
	private Company company;

	@OneToMany(mappedBy = "shop")
	private List<Staff> staffs;

	@OneToMany(mappedBy = "shop")
	private List<Job> jobs;

	@OneToMany(mappedBy = "shop")
	@JsonIgnoreProperties({ "shop", "staffs", "candidate" })
	private List<ShopCandidateRelation> shopCandidateRelations;

	public int getIdShop() {
		return idShop;
	}

	public void setIdShop(int idShop) {
		this.idShop = idShop;
	}

	public String getNameShop() {
		return nameShop;
	}

	public void setNameShop(String nameShop) {
		this.nameShop = nameShop;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<Staff> getStaffs() {
		return staffs;
	}

	public void setStaffs(List<Staff> staffs) {
		this.staffs = staffs;
	}

	public String getAddressShop() {
		return addressShop;
	}

	public void setAddressShop(String addressShop) {
		this.addressShop = addressShop;
	}

	public String getDescriptionShop() {
		return descriptionShop;
	}

	public void setDescriptionShop(String descriptionShop) {
		this.descriptionShop = descriptionShop;
	}

	public String getNearestStation() {
		return nearestStation;
	}

	public void setNearestStation(String nearestStation) {
		this.nearestStation = nearestStation;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getLogoShop() {
		return logoShop;
	}

	public void setLogoShop(String logoShop) {
		this.logoShop = logoShop;
	}

	public String getUserIdLine() {
		return userIdLine;
	}

	public void setUserIdLine(String userIdLine) {
		this.userIdLine = userIdLine;
	}

	public String getChannelToken() {
		return channelToken;
	}

	public void setChannelToken(String channelToken) {
		this.channelToken = channelToken;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public List<ShopCandidateRelation> getShopCandidateRelations() {
		return shopCandidateRelations;
	}

	public void setShopCandidateRelations(List<ShopCandidateRelation> shopCandidateRelations) {
		this.shopCandidateRelations = shopCandidateRelations;
	}

}
