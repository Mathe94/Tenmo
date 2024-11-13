package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.model.*;
import jakarta.validation.Valid;

import com.techelevator.tenmo.exception.DaoException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

/**
 * Controller to authenticate users.
 */
@RestController
public class AuthenticationController {
    private final TransferService transferService;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDao userDao;


    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao, TransferService transferService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
        this.transferService = transferService;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResponseDto login(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);

        User user;
        try {
            user = userDao.getUserByUsername(loginDto.getUsername());
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect.");
        }

        return new LoginResponseDto(jwt, user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDto newUser) {
        try {
            if (userDao.getUserByUsername(newUser.getUsername()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists.");
            } else {
                userDao.createUser(newUser);
            }
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed.");
        }
    }


    @GetMapping("/balance")
    public Balance getBalance(Principal principal) {

        String username = principal.getName();
        User user = userDao.getUserByUsername(username);

        return userDao.getBalanceByUserId(user.getId());
    }

    @PostMapping("/transfers/send")
    public void sendBucks(@Valid @RequestBody TransferDTO transfer) {

        if (transfer.getAccountFrom() == transfer.getAccountTo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send TE Bucks to yourself.");
        }


        Balance senderBalance = userDao.getBalanceByUserId(transfer.getAccountFrom());


        if (transfer.getTransferAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer amount must be greater than zero.");
        }


        if (senderBalance.getAmount().compareTo(BigDecimal.valueOf(transfer.getTransferAmount())) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds.");
        }


        userDao.updateBalance(
                transfer.getAccountFrom(),
                senderBalance.getAmount().subtract(BigDecimal.valueOf(transfer.getTransferAmount()))
        );

        Balance receiverBalance = userDao.getBalanceByUserId(transfer.getAccountTo());
        userDao.updateBalance(
                transfer.getAccountTo(),
                receiverBalance.getAmount().add(BigDecimal.valueOf(transfer.getTransferAmount()))
        );
    }

    @GetMapping("/transfers")
    public List getTransferList (Principal principal) {
        String username = principal.getName();
        return transferService.getTransfersForUser(username);
    }

    @GetMapping("/transfers")
    public TransferDTO getTransfersById (int transferId) {
    TransferDTO transfer = transferService.getTransferById(transferId);
    if (transfer == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found");
    }
    return transfer;
    }







    }




