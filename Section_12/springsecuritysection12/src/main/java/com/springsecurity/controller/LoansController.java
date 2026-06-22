package com.springsecurity.controller;

import com.springsecurity.model.Loans;
import com.springsecurity.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoansController {

    private final LoanRepository loanRepository;

    @GetMapping("/myLoans")
    //Once method execution completes while sending response it will check role
    @PostAuthorize("hasRole('USER')")// Only users with 'USER' role can access this method
    public List<Loans> getLoanDetails(@RequestParam long id) {
        List<Loans> loans = loanRepository.findByCustomerIdOrderByStartDtDesc(id);
        if (loans != null) {
            return loans;
        } else {
            return null;
        }
    }

}
