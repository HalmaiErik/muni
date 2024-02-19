package com.muni.bankaccountdata.validator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.muni.bankaccountdata.db.entity.Customer;
import com.muni.bankaccountdata.db.repository.CustomerRepository;
import com.muni.bankaccountdata.exception.ApiException;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidator {

    private static final String FIREBASE_AND_REQUEST_EMAILS_MISMATCH = "Request email %s does not match the token email %s";
    private static final String FIREBASE_ERROR = "Error when connecting to firebase to retrieve user email";
    private static final String REQUIRED_CUSTOMER_EXCEPTION_MSG = "No customer found with email %s";

    private final CustomerRepository customerRepository;
    private final FirebaseAuth firebaseAuth;

    public CustomerValidator(CustomerRepository customerRepository, FirebaseAuth firebaseAuth) {
        this.customerRepository = customerRepository;
        this.firebaseAuth = firebaseAuth;
    }

    public Customer validateAndGetRequiredCustomer(String token) {
        String email = getEmailFromToken(token);

        return customerRepository.findCustomerByEmail(email)
                .orElseThrow(() -> new ApiException(String.format(REQUIRED_CUSTOMER_EXCEPTION_MSG, email)));
    }

    public String getEmailFromToken(String token) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);

            return firebaseAuth.getUser(decodedToken.getUid()).getEmail();
        } catch (FirebaseAuthException e) {
            throw new ApiException(e.getMessage());
        }
    }
}
