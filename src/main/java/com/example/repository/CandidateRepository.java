package com.example.repository;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.Candidate;

@Repository
@RepositoryRestResource
public interface CandidateRepository extends JpaRepository<Candidate, Serializable> {

	@Query(value = "select s from Candidate s where s.personInCharge.idUser=:idAdmin and s.userName like %:candName%", countQuery = "select count(*) from Candidate s where s.personInCharge.idUser=:idAdmin and s.userName like %:candName% ")
	public Page<Candidate> getCandidateListByIdAdminPaginated(@Param("idAdmin") int idAdmin,
			@Param("candName") String candName, Pageable pageable);

	@Query(value = "select s from Candidate s where  s.userName like  %:candName%", countQuery = "select count(*) from Candidate s where  s.userName like %:candName%")
	public Page<Candidate> searchByCandNamePaginated(@Param("candName") String candName, Pageable pageable);

}
