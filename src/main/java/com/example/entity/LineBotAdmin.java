package com.example.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class LineBotAdmin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idLineBotAdmin;

	private String userIdLine;
	private String channelToken;

	@OneToMany(mappedBy = "lineBotAdmin")
	private List<Admin> admins;

	public int getIdLineBotAdmin() {
		return idLineBotAdmin;
	}

	public void setIdLineBotAdmin(int idLineBotAdmin) {
		this.idLineBotAdmin = idLineBotAdmin;
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

	public List<Admin> getAdmins() {
		return admins;
	}

	public void setAdmins(List<Admin> admins) {
		this.admins = admins;
	}

}
