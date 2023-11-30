package com.smart.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;



@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
//	 method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("username -->"+ userName);
		// getting the user using userName(email) -->
		User user = userRepository.getUserByUserName(userName);
		// System.out.println("User -->"+user);
		model.addAttribute("user", user);
		
	}
	// handler  for making user_dashboard
	@RequestMapping("/index")
	public String dashboard( Model model, Principal principal) {
		
		return "normal/user_dashboard";
	}
	// Open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "add-contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
	
	// processing add- contact handler
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
		@RequestParam("profileImage") MultipartFile file,
		Principal principal, HttpSession session) {
		try {
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		// processing and uploading file(image) -->
		
		if(file.isEmpty()) {
			contact.setImage("contact.png");
			System.out.println("Yoy haven't chosen any image file");
		}
		else {
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/img").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+ File.separator +
					file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image is uploaded successfully-->");
		}
		
		contact.setUser(user);
		user.getContacts().add(contact);
		userRepository.save(user);
		// success message --> (to view)
		session.setAttribute("message", new Message("Contact added Successfully! Add More.. ", "success"));
		
		}catch (Exception e) {
			System.out.println(e.getMessage());
			// error message to view -->
			session.setAttribute("message", new Message("Something Went Wrong! Try Again.. ", "danger"));
			
		}
		return "normal/add_contact_form";
	}
	
	// Show- contacts handler -->
	
//	 per page -7[n]
//	current page -0[page]
	@GetMapping("/show-contacts/{page}")
	public String shoeContacts(@PathVariable("page") Integer page,
			Model model , Principal principal) {
		
		model.addAttribute("title", "show-contacts-SCM");
		
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		int userId = user.getId();
		
		// creating PgeRequest object by using Pageable as instance.
	  Pageable pageable = PageRequest.of(page, 7);
	  
		Page<Contact> contacts = contactRepository.findContactsByUser(userId, pageable); 
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	// creating handler for showing particular contact detail-->
	@RequestMapping("/contact/{cid}")
	public String showContactDetail(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "contact-details-SCM");
		 Optional<Contact> contactOptional = this.contactRepository.findById(cid); 
		 Contact contact = contactOptional.get();
		 model.addAttribute("contact", contact);
		 
		return "normal/contact_detail";
	}
	
	// handler for deleting contact -->
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, HttpSession session, 
			Principal principal ) {
		
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		 Contact contact = this.contactRepository.findById(cid).get();
		 
			try {
				// checking the user--> 
				if(user.getId() == contact.getUser().getId()) {
				 user.getContacts().remove(contact);
				 session.setAttribute("message", new Message("You Have Successfully Deleted the Contact !", "danger")); 
				 this.userRepository.save(user);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} 
		
		return "redirect:/user/show-contacts/0";
	}
	
	
	// open update form handler -->
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid, Model model) {
		
		model.addAttribute("title", "Update-Form - SCM");
		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);
		
		return "normal/update_form";
	}
	
	// update contact handler -->
	 @PostMapping("process-update")
	public String updateHandler(@ModelAttribute Contact contact, Principal principal,
			HttpSession session, @RequestParam("profileImage") MultipartFile file) {
        try {
        	// old contact details -->
        	Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();
        	
        	if(!file.isEmpty()) {
        		// first delete old photo -->
        		File deleteFile = new ClassPathResource("static/img").getFile();
        		File file1 = new File(deleteFile, oldContactDetail.getImage());
        		file1.delete();
        		
        		
        		// next update photo -->
        		File saveFile = new ClassPathResource("static/img").getFile();
    			
    			Path path = Paths.get(saveFile.getAbsolutePath()+ File.separator +file.getOriginalFilename()); 
    			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    			contact.setImage(file.getOriginalFilename());
    			System.out.println("image updataed successfully -->");
        		
        	}
        	else {
        		contact.setImage(oldContactDetail.getImage());
        	}
		User user = userRepository.getUserByUserName(principal.getName());
		contact.setUser(user);
		contactRepository.save(contact);
		
		// sending message -->
		session.setAttribute("message", new Message("Contact Details updated Successfully !", "success"));
		
	} catch (Exception e) {
		System.out.println(e.getMessage());
	}
		return "redirect:/user/contact/"+ contact.getCid();
	}
	 
	 // Your Profile Handler -->
	 @GetMapping("/profile")
	 public String yourProfile(Model model) {
		 model.addAttribute("title", "your-profile - SCM");
		 return "normal/profile";
	 }


}
