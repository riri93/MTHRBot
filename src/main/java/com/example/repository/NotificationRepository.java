package com.example.repository;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.Notification;

@Repository
@RepositoryRestResource
public interface NotificationRepository extends JpaRepository<Notification, Serializable> {
	@Query(value = "select s from Notification s where  s.admin.idUser=:idAdmin and  s.notificationDate= CURRENT_DATE", countQuery = "select count(*) from Notification s where  s.admin.idUser=:idAdmin  and  s.notificationDate= CURRENT_DATE")
	public Page<Notification> getLastNotificationByAdmin(@Param("idAdmin") int idAdmin, Pageable pageable);
}
