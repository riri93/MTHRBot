package com.example.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.Admin;

@Repository
@RepositoryRestResource
public interface AdminRepository extends JpaRepository<Admin, Serializable> {

	@Query("SELECT a FROM Admin a where LOWER(a.userName) = LOWER(:name)")
	public Admin findAdminByName(@Param("name") String name);

}
