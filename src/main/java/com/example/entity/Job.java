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
public class Job implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idJob;

	private String positionName;
	private String positionCategory;
	private String salaryDetail;
	private String jobDetails;
	private String jobType;
	private int numberStaffNeeded;
	private double salary;
	private double hourlyWage;

	@ManyToOne
	@JoinColumn(name = "idShop", referencedColumnName = "idShop")
	@JsonIgnoreProperties({ "jobs" })
	private Shop shop;

	@OneToMany(mappedBy = "job")
	@JsonIgnoreProperties({ "job" })
	private List<JobCandidateRelation> jobCandidateRelations;

	public int getIdJob() {
		return idJob;
	}

	public void setIdJob(int idJob) {
		this.idJob = idJob;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public List<JobCandidateRelation> getJobCandidateRelations() {
		return jobCandidateRelations;
	}

	public void setJobCandidateRelations(List<JobCandidateRelation> jobCandidateRelations) {
		this.jobCandidateRelations = jobCandidateRelations;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getPositionCategory() {
		return positionCategory;
	}

	public void setPositionCategory(String positionCategory) {
		this.positionCategory = positionCategory;
	}

	public String getSalaryDetail() {
		return salaryDetail;
	}

	public void setSalaryDetail(String salaryDetail) {
		this.salaryDetail = salaryDetail;
	}

	public String getJobDetails() {
		return jobDetails;
	}

	public void setJobDetails(String jobDetails) {
		this.jobDetails = jobDetails;
	}

	public int getNumberStaffNeeded() {
		return numberStaffNeeded;
	}

	public void setNumberStaffNeeded(int numberStaffNeeded) {
		this.numberStaffNeeded = numberStaffNeeded;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public double getHourlyWage() {
		return hourlyWage;
	}

	public void setHourlyWage(double hourlyWage) {
		this.hourlyWage = hourlyWage;
	}

}
