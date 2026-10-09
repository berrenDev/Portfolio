package dev.berren.portfolio.profile.internal;

import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ProfileRepository extends Repository<Profile, Short> {

	Optional<Profile> findById(Short id);

	default Optional<Profile> findSingleton() {
		return findById(Profile.SINGLETON_ID);
	}

}
