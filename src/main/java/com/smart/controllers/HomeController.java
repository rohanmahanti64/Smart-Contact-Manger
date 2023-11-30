package com.smart.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;



@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
@Autowired	
private	UserRepository userRepository; 
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "home- Smart Contact Manager");
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "about- Smart Contact Manager");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "signup- Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	// Handler for registering user-->
	@PostMapping("/do_register")
	public String registerUser(@Valid  @ModelAttribute("user")User user, BindingResult result1 , 

		@RequestParam(value = "checkbox", defaultValue = "false") boolean checkbox
			, Model model,HttpSession session) {
		try {
			if(checkbox!= true) {
				System.out.println("You have not agreed terms and conditions");
				throw new Exception("You have not agreed terms and conditions");
			}
			
			if(result1.hasErrors()) {
				System.out.println(result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassward(passwordEncoder.encode(user.getPassward()));
			
			System.out.println(checkbox);
			System.out.println(user);
			this.userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered ! ","alert-success"));
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", 
		new Message("Something Went Wrong ! "+e.getMessage(), "alert-danger"));
			return "signup";
		}
	
	}
	// Handler for custom login-->
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "login - Contact Manager");
		return "login";
	}

}
