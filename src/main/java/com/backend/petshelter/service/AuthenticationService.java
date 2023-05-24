package com.backend.petshelter.service;

import com.backend.petshelter.dto.AccountDTO;
import com.backend.petshelter.dto.AccountSignIn;
import com.backend.petshelter.model.Account;

public interface AuthenticationService {
    abstract Account signInAndReturnJWT(AccountSignIn signInRequest);
}
