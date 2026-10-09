package dev.berren.portfolio.profile.internal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Locale;

@Converter
class SocialPlatformConverter implements AttributeConverter<SocialPlatform, String> {

	@Override
	public String convertToDatabaseColumn(SocialPlatform platform) {
		return platform == null ? null : platform.name().toLowerCase(Locale.ROOT);
	}

	@Override
	public SocialPlatform convertToEntityAttribute(String value) {
		return value == null ? null : SocialPlatform.valueOf(value.toUpperCase(Locale.ROOT));
	}

}
