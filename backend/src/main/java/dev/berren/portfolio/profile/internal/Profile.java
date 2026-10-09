package dev.berren.portfolio.profile.internal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Perfil del portfolio. Solo existe un registro: la base de datos lo garantiza con
 * {@code CHECK (id = 1)}.
 */
@Entity
@Table(name = "profile")
public class Profile {

	static final short SINGLETON_ID = 1;

	@Id
	private Short id;

	@Column(name = "full_name", nullable = false, length = 100)
	private String fullName;

	@Column(nullable = false, length = 150)
	private String headline;

	@Column(nullable = false, columnDefinition = "text")
	private String summary;

	@Column(length = 100)
	private String location;

	@Column(name = "avatar_url", length = 2048)
	private String avatarUrl;

	@Column(name = "cv_url", length = 2048)
	private String cvUrl;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "profile_id", nullable = false)
	@OrderBy("position")
	private List<SocialLink> socialLinks = new ArrayList<>();

	protected Profile() {
	}

	public Short getId() {
		return id;
	}

	public String getFullName() {
		return fullName;
	}

	public String getHeadline() {
		return headline;
	}

	public String getSummary() {
		return summary;
	}

	public String getLocation() {
		return location;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public String getCvUrl() {
		return cvUrl;
	}

	public List<SocialLink> getSocialLinks() {
		return socialLinks;
	}

}
