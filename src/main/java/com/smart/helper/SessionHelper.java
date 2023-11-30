package com.smart.helper;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



@Component
public class SessionHelper {
	public void removeMassagesFromSession() {
		try {
			System.out.println("removing massages from session");
			
			HttpSession session = ((ServletRequestAttributes)RequestContextHolder.
					getRequestAttributes()).getRequest().getSession();
			session.removeAttribute("message");
		} catch (Exception e) {
		   e.printStackTrace();
		}
	}

}
