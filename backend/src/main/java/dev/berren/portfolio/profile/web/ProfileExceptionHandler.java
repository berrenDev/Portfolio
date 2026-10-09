package dev.berren.portfolio.profile.web;

import dev.berren.portfolio.profile.ProfileNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones del módulo a Problem Details. Precede al handler global, que
 * capturaría cualquier {@link Exception} como 500.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ProfileExceptionHandler {

	@ExceptionHandler(ProfileNotFoundException.class)
	ProblemDetail handleProfileNotFound(ProfileNotFoundException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
	}

}
