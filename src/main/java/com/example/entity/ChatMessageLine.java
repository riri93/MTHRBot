package com.example.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class ChatMessageLine implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idChatMessageLine;

	private int messageDirection;
	private String messageText;
	private Date messageDate;
	private boolean readState;

	@ManyToOne
	@JoinColumn(name = "idChatLineAdmin", referencedColumnName = "idChatLineAdmin")
	@JsonIgnoreProperties("chatMessageLines")
	private ChatLineAdmin chatLineAdmin;

	public int getIdChatMessageLine() {
		return idChatMessageLine;
	}

	public void setIdChatMessageLine(int idChatMessageLine) {
		this.idChatMessageLine = idChatMessageLine;
	}

	public ChatLineAdmin getChatLineAdmin() {
		return chatLineAdmin;
	}

	public void setChatLineAdmin(ChatLineAdmin chatLineAdmin) {
		this.chatLineAdmin = chatLineAdmin;
	}

	public int getMessageDirection() {
		return messageDirection;
	}

	public void setMessageDirection(int messageDirection) {
		this.messageDirection = messageDirection;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public Date getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}

	public boolean isReadState() {
		return readState;
	}

	public void setReadState(boolean readState) {
		this.readState = readState;
	}

}
