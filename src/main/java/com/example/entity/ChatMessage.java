package com.example.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ChatMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idChatMessage;
	
	private int messageDirection;
	private String messageText;
	private Date messageDate;
	private boolean readState;
	
	@ManyToOne
	@JoinColumn(name = "idChat", referencedColumnName = "idChat")
	private Chat chat;


	public int getIdChatMessage() {
		return idChatMessage;
	}

	public void setIdChatMessage(int idChatMessage) {
		this.idChatMessage = idChatMessage;
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

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

}
