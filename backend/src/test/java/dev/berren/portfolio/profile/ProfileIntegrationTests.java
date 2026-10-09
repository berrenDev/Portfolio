package dev.berren.portfolio.profile;

import static org.assertj.core.api.Assertions.assertThat;

import dev.berren.portfolio.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ProfileIntegrationTests {

	@Autowired
	private MockMvcTester mvc;

	@Test
	@Sql("/sql/delete-profile.sql")
	void returnsNotFoundProblemWhenProfileDoesNotExist() {
		assertThat(mvc.get().uri("/api/v1/profile"))
			.hasStatus(HttpStatus.NOT_FOUND)
			.hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
			.bodyJson()
			.isLenientlyEqualTo("""
				{
				  "title": "Not Found",
				  "status": 404,
				  "detail": "Profile has not been created yet.",
				  "instance": "/api/v1/profile"
				}
				""");
	}

	@Test
	@Sql({ "/sql/delete-profile.sql", "/sql/insert-profile.sql" })
	void returnsProfileWithSocialLinksInDisplayOrder() {
		assertThat(mvc.get().uri("/api/v1/profile"))
			.hasStatusOk()
			.hasContentType(MediaType.APPLICATION_JSON)
			.bodyJson()
			.isStrictlyEqualTo("""
				{
				  "fullName": "Ana García López",
				  "headline": "Backend Developer · Java & Spring",
				  "summary": "Desarrolladora backend centrada en **Java**.",
				  "location": "Madrid, España",
				  "avatarUrl": "https://cdn.example.dev/img/avatar.webp",
				  "cvUrl": "https://cdn.example.dev/files/cv.pdf",
				  "socialLinks": [
				    { "platform": "github", "url": "https://github.com/example" },
				    { "platform": "linkedin", "url": "https://www.linkedin.com/in/example" },
				    { "platform": "email", "url": "mailto:hello@example.dev" }
				  ]
				}
				""");
	}

	@Test
	void keepsSpringMvcStatusForFrameworkErrors() {
		assertThat(mvc.post().uri("/api/v1/profile"))
			.hasStatus(HttpStatus.METHOD_NOT_ALLOWED)
			.hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
	}

}
