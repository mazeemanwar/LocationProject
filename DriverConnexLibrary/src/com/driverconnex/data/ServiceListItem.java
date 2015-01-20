package com.driverconnex.data;

public class ServiceListItem {
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhonename() {
		return phonename;
	}

	public void setPhonename(String phonename) {
		this.phonename = phonename;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailname() {
		return emailname;
	}

	public void setEmailname(String emailname) {
		this.emailname = emailname;
	}

	private String phone;
	private String phonename;
	private String email;
	private String emailname;
	private String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
