package com.messaging.rcs.model;

public class ResetPasswordModel {
	private Long userId;
	private String newPassword;
	private String oldPassword;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	@Override
	public String toString() {
		return "ResetPasswordModel [userId=" + userId + ", newPassword=" + newPassword + ", oldPassword=" + oldPassword
				+ "]";
	}

}
