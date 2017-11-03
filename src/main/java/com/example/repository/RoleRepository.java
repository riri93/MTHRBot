package com.example.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.entity.Role;


@Repository
@RepositoryRestResource
public interface RoleRepository extends JpaRepository<Role, Serializable> {


	
	@Query("SELECT r FROM Role r where LOWER(r.name) = LOWER(:name)")
	public Role findRoleByName(@Param("name") String name);

}
