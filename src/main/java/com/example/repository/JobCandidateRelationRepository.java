package com.example.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.JobCandidateRelation;

@Repository
@RepositoryRestResource
public interface JobCandidateRelationRepository extends JpaRepository<JobCandidateRelation, Serializable> {

	@Query("Select jc from JobCandidateRelation jc where jc.job.shop.idShop=:idShop and  jc.jobCandidateRelationPK.idCandidate=:idCandidate and  jc.applied = true")
	List<JobCandidateRelation> getAppliedJobsByShopAndByCandidate(@Param("idShop") int idShop,
			@Param("idCandidate") int idCandidate);

	@Query("Select jc from JobCandidateRelation jc where jc.applied = true")
	List<JobCandidateRelation> getAllAppliedCandidates();

}
