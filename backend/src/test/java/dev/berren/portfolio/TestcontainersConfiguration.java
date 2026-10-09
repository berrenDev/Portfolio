package dev.berren.portfolio;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import org.yaml.snakeyaml.Yaml;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	private static final Path COMPOSE_FILE = Path.of("compose.yaml");

	/** Scripts de inicialización compartidos con compose.yaml (simulan Supabase). */
	private static final Path INIT_SCRIPTS = Path.of("docker/postgres/init");

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		return new PostgreSQLContainer(DockerImageName.parse(postgresImageFromCompose()))
			.withCopyFileToContainer(MountableFile.forHostPath(INIT_SCRIPTS), "/docker-entrypoint-initdb.d/");
	}

	/**
	 * Lee la imagen de PostgreSQL de {@code compose.yaml} para que la versión se defina en un único sitio.
	 */
	@SuppressWarnings("unchecked")
	private static String postgresImageFromCompose() {
		try (InputStream in = Files.newInputStream(COMPOSE_FILE)) {
			Map<String, Object> compose = new Yaml().load(in);
			Map<String, Object> services = (Map<String, Object>) compose.get("services");
			Map<String, Object> postgres = (Map<String, Object>) services.get("postgres");
			return (String) postgres.get("image");
		}
		catch (IOException ex) {
			throw new UncheckedIOException("Cannot read PostgreSQL image from " + COMPOSE_FILE.toAbsolutePath(), ex);
		}
	}

}
