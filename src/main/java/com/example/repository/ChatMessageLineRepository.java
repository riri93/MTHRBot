package com.example.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.ChatMessageLine;

@Repository
@RepositoryRestResource
public interface ChatMessageLineRepository extends JpaRepository<ChatMessageLine, Serializable> {

	@Query("Select cm from ChatMessageLine cm where cm.chatLineAdmin.idChatLineAdmin=:idChatLineAdmin ORDER BY cm.messageDate DESC")
	public List<ChatMessageLine> listAllChatMessagesByIdChatAdmin(@Param("idChatLineAdmin") int idChatLineAdmin);

	@Query("Select cm from ChatMessageLine cm where cm.chatLineAdmin.idChatLineAdmin=:idChatLineAdmin ORDER BY cm.messageDate DESC")
	public ChatMessageLine getLastChatMessageLineByIdChatAdmin(int idChatLineAdmin);

}
