package com.cryptoag.orderbook.controller;

import java.util.List;

import com.cryptoag.orderbook.model.Account;
import com.cryptoag.orderbook.service.OrderbookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AccountController {

  @Autowired 
  OrderbookService orderbookService;

	@GetMapping("/accounts/{id}")
  Account fetchAccountDetails(@PathVariable Integer id){
	  return orderbookService.fetchAccountDetails(id);
  }
    
  @PostMapping("/account")
  public ResponseEntity<Object> createAccount(@RequestBody Account account){
    log.info(account.toString());
    try{
      return new ResponseEntity<>(orderbookService.createAccount(account),HttpStatus.OK);
    }catch(Exception e){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }


  @GetMapping("/accounts")
  public List<Account> fetchAccounts(){
	  return orderbookService.ListAccounts();
  }

}