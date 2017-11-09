package com.example.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.ShopCandidateRelation;

@Repository
@RepositoryRestResource
public interface ShopCandidateRelationRepository extends JpaRepository<ShopCandidateRelation, Serializable> {

	@Query("Select sc from ShopCandidateRelation sc where sc.shopCandidateRelationPK.idShop=:idShop and  sc.shopCandidateRelationPK.idCandidate=:idCandidate")
	List<ShopCandidateRelation> getShopCandidateRelationList(@Param("idShop") int idShop,
			@Param("idCandidate") int idCandidate);

	@Query("Select sc from ShopCandidateRelation sc where sc.candidate.userLineId =:lineID")
	ShopCandidateRelation findShopCandidateRelationByLineID(@Param("lineID") String lineID);

}
