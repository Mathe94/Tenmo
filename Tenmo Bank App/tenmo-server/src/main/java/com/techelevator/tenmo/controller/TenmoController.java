//package com.techelevator.tenmo.controller;
//import com.techelevator.tenmo.model.*;
//import jakarta.validation.Valid;
//
//import com.techelevator.tenmo.exception.DaoException;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import com.techelevator.tenmo.dao.UserDao;
//import com.techelevator.tenmo.security.jwt.TokenProvider;
//import org.springframework.web.server.ResponseStatusException;
//
//import com.techelevator.tenmo.model.Balance;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@RestController
//public class TenmoController(UserDao userDao) {
//
//   // private final TokenProvider tokenProvider;
//   /// private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    private final UserDao userDao;
//    this.UserDao = userDao;
//
//
//    @GetMapping("/balance/{user_id}")
//    public Balance getBalanceByUserId(@RequestParam int id) {
//        return userDao.getBalanceByUserId(id);
//    }
//
//
//
//}
