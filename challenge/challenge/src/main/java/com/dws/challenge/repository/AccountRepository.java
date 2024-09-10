package com.dws.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing and managing Account entities.
 * 
 * This interface extends JpaRepository to provide CRUD operations and query methods for the Account entity.
 * 
 * 
 * @author tanaydas
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}