package com.example.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.Job;

@Repository
@RepositoryRestResource
public interface JobRepository extends JpaRepository<Job, Serializable> {

	@Query("SELECT j FROM Job j where lower(j.shop.addressShop) like lower(CONCAT('%',:address,'%')) or lower(j.shop.nearestStation) like lower(CONCAT('%',:address,'%'))")
	public List<Job> findByAreaOrStation(@Param("address") String address);

	@Query("SELECT j FROM Job j where lower(j.shop.addressShop) like lower(CONCAT('%',:address,'%')) or lower(j.shop.nearestStation) like lower(CONCAT('%',:address,'%')) and j.salary >= :salary")
	public List<Job> findByAreaOrStationAndSalary(@Param("address") String address, @Param("salary") double salary);

}
