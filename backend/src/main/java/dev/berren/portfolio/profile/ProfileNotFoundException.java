package dev.berren.portfolio.profile;

public class ProfileNotFoundException extends RuntimeException {

	ProfileNotFoundException() {
		super("Profile has not been created yet.");
	}

}
