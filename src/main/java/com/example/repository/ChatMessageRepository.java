package com.example.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.ChatMessage;

@Repository
@RepositoryRestResource
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Serializable> {

	@Query("Select cm from ChatMessage cm where cm.chat.idChat=:idChat ORDER BY cm.messageDate ASC")
	public Iterable<ChatMessage> listAllChatMessagesByIdChat(@Param("idChat") int idChat);

}
