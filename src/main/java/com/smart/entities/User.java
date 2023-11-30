package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "USER")
public class User {
@Id
@GeneratedValue(strategy = GenerationType.AUTO)	
private int id;
@NotBlank(message = "Name Field Required ! ")
private String name;
@Column(unique = true) // making the email field unique
@Email
private String email;

private String passward;
private String role;
private boolean enabled;
private String imageUrl;
@Column(length = 500) // taking max 500 words as input
private String about;
@OneToMany(cascade = CascadeType.ALL , mappedBy = "user", orphanRemoval = true)
// cascade is for hibernate cascading
// mapped by is for creating one foreign key in one table
// orphanremoval for avoiding infinite recursion 
private List<Contact> contacts = new ArrayList<>();

public List<Contact> getContacts() {
	return contacts;
}
public void setContacts(List<Contact> contacts) {
	this.contacts = contacts;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getPassward() {
	return passward;
}
public void setPassward(String passward) {
	this.passward = passward;
}
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}
public boolean isEnabled() {
	return enabled;
}
public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}
public String getImageUrl() {
	return imageUrl;
}
public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}
public String getAbout() {
	return about;
}
public void setAbout(String about) {
	this.about = about;
}
@Override
public String toString() {
	return "User [id=" + id + ", name=" + name + ", email=" + email + ", passward=" + passward + ", role=" + role
			+ ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", about=" + about + ", contacts=" + contacts + "]";
}

}
