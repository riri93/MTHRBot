package com.example.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Chat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idChat;

	@OneToOne
	@JoinColumns({ @JoinColumn(name = "idJob", referencedColumnName = "idJob"),
			@JoinColumn(name = "idCandidate", referencedColumnName = "idCandidate") })
	private JobCandidateRelation jobCandidateRelation;

	@OneToMany(mappedBy = "chat")
	private List<ChatMessage> chatMessages;

	public int getIdChat() {
		return idChat;
	}

	public void setIdChat(int idChat) {
		this.idChat = idChat;
	}

	public JobCandidateRelation getJobCandidateRelation() {
		return jobCandidateRelation;
	}

	public void setJobCandidateRelation(JobCandidateRelation jobCandidateRelation) {
		this.jobCandidateRelation = jobCandidateRelation;
	}

	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(List<ChatMessage> chatMessages) {
		this.chatMessages = chatMessages;
	}

}
