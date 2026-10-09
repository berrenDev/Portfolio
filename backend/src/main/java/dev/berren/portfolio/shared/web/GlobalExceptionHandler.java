package dev.berren.portfolio.shared.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Último recurso para errores no controlados.
 * <p>
 * Extiende {@link ResponseEntityExceptionHandler} para que las excepciones que Spring MVC ya sabe
 * resolver (400, 404 de recurso, 405, 415…) mantengan su código y se devuelvan como Problem Details.
 * Cualquier otra excepción se registra completa y se responde con un 500 genérico, sin exponer
 * detalles internos.
 * <p>
 * Tiene la menor precedencia para que los handlers específicos de cada módulo se evalúen antes.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	ProblemDetail handleUnexpected(Exception ex) {
		log.error("Unhandled exception", ex);
		return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
	}

}
