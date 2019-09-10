package com.telkomsel.login.login01.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telkomsel.login.login01.model.Login;

@Controller
public class LoginController {
	@Autowired
	Login lg;
	
	@RequestMapping(value="/Login-API", method = RequestMethod.GET)
	@ResponseBody
	public String APILOGIN(@RequestParam String username, @RequestParam String password) {
		String resp = lg.login(username, password);
		return resp;
	}
}
