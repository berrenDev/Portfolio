package dev.berren.portfolio.profile.internal;

import java.net.URI;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Convierte las entidades del módulo en los DTOs generados desde el contrato.
 * Los tipos del contrato se referencian con su nombre completo porque coinciden
 * con los de las entidades.
 * <p>
 * {@code unmappedTargetPolicy = ERROR}: si el contrato añade un campo y no se mapea, falla la compilación.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProfileMapper {

	dev.berren.portfolio.api.model.Profile toDto(Profile profile);

	dev.berren.portfolio.api.model.SocialLink toDto(SocialLink socialLink);

	dev.berren.portfolio.api.model.SocialPlatform toDto(SocialPlatform platform);

	default URI toUri(String value) {
		return value == null ? null : URI.create(value);
	}

}
