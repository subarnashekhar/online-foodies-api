package com.techsmart.foodiesapi.service;

import com.techsmart.foodiesapi.io.UserRequest;
import com.techsmart.foodiesapi.io.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest request);

    String findByUserId();
}
