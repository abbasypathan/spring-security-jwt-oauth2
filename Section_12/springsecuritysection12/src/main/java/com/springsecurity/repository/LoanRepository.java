package com.springsecurity.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import com.springsecurity.model.Loans;

@Repository
public interface LoanRepository extends CrudRepository<Loans, Long> {

    // It will validate role before calling the method
    @PreAuthorize("hasRole('USER')") // Only users with 'USER' role can access this method
    List<Loans> findByCustomerIdOrderByStartDtDesc(long customerId);

}
