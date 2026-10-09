package dev.berren.portfolio.profile;

import dev.berren.portfolio.api.ProfileApi;
import dev.berren.portfolio.api.model.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ProfileController implements ProfileApi {

	private final ProfileService profileService;

	ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}

	@Override
	public ResponseEntity<Profile> getProfile() {
		return ResponseEntity.ok(profileService.getProfile());
	}

}
