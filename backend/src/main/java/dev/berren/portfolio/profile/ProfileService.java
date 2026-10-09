package dev.berren.portfolio.profile;

import dev.berren.portfolio.api.model.Profile;
import dev.berren.portfolio.profile.internal.ProfileMapper;
import dev.berren.portfolio.profile.internal.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

	private final ProfileRepository profileRepository;
	private final ProfileMapper profileMapper;

	ProfileService(ProfileRepository profileRepository, ProfileMapper profileMapper) {
		this.profileRepository = profileRepository;
		this.profileMapper = profileMapper;
	}

	/**
	 * Devuelve el perfil publicado.
	 *
	 * @throws ProfileNotFoundException si todavía no se ha creado
	 */
	@Transactional(readOnly = true)
	public Profile getProfile() {
		return profileRepository.findSingleton()
			.map(profileMapper::toDto)
			.orElseThrow(ProfileNotFoundException::new);
	}

}
