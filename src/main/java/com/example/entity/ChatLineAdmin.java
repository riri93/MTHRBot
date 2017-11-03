package com.example.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class ChatLineAdmin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idChatLineAdmin;

	@OneToMany(mappedBy = "chatLineAdmin")
	private List<ChatMessageLine> ChatMessageLines;

	public int getIdChatLineAdmin() {
		return idChatLineAdmin;
	}

	public void setIdChatLineAdmin(int idChatLineAdmin) {
		this.idChatLineAdmin = idChatLineAdmin;
	}

	public List<ChatMessageLine> getChatMessageLines() {
		return ChatMessageLines;
	}

	public void setChatMessageLines(List<ChatMessageLine> chatMessageLines) {
		ChatMessageLines = chatMessageLines;
	}

}
