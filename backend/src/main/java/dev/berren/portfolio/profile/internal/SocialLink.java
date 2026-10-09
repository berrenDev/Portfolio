package dev.berren.portfolio.profile.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "social_link")
public class SocialLink {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Convert(converter = SocialPlatformConverter.class)
	@Column(nullable = false, length = 20)
	private SocialPlatform platform;

	@Column(nullable = false, length = 2048)
	private String url;

	/** Orden de presentación (0 = primero). */
	@Column(nullable = false)
	private int position;

	protected SocialLink() {
	}

	public Long getId() {
		return id;
	}

	public SocialPlatform getPlatform() {
		return platform;
	}

	public String getUrl() {
		return url;
	}

	public int getPosition() {
		return position;
	}

}
