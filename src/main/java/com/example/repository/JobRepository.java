package com.example.repository;

import java.io.Serializable;
import java.util.Date;
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

	@Query("SELECT j FROM Job j where (j.hourlyWage >= :salary) and (lower(j.shop.addressShop) like lower(CONCAT('%',:address,'%')) or lower(j.shop.nearestStation) like lower(CONCAT('%',:address,'%')))")
	public List<Job> findByAreaOrStationAndSalary(@Param("address") String address, @Param("salary") double salary);

	@Query("SELECT j FROM Job j where (j.startWorkingTime >= :startWorkingTime and j.finishWorkingTime <= :finishWorkingTime) and (lower(j.shop.addressShop) like lower(CONCAT('%',:address,'%')) or lower(j.shop.nearestStation) like lower(CONCAT('%',:address,'%')))")
	public List<Job> findByAreaOrStationAndWorkTime(@Param("address") String addressToSearch,
			@Param("startWorkingTime") Date startWorkingTime, @Param("finishWorkingTime") Date finishWorkingTime);

}
