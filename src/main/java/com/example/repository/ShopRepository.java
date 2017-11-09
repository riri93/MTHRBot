package com.example.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.Shop;

@Repository
@RepositoryRestResource
public interface ShopRepository extends JpaRepository<Shop, Serializable> {

	@Query(value = "select s from Shop s where s.channelToken =:token")
	public Shop findByChannelToken(@Param("token") String token);
}
