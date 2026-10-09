INSERT INTO portfolio.profile (id, full_name, headline, summary, location, avatar_url, cv_url)
VALUES (1, 'Ana García López', 'Backend Developer · Java & Spring',
        'Desarrolladora backend centrada en **Java**.', 'Madrid, España',
        'https://cdn.example.dev/img/avatar.webp', 'https://cdn.example.dev/files/cv.pdf');

-- Insertados desordenados a propósito: la respuesta debe seguir la columna position.
INSERT INTO portfolio.social_link (profile_id, platform, url, position) VALUES (1, 'email', 'mailto:hello@example.dev', 2);
INSERT INTO portfolio.social_link (profile_id, platform, url, position) VALUES (1, 'github', 'https://github.com/example', 0);
INSERT INTO portfolio.social_link (profile_id, platform, url, position) VALUES (1, 'linkedin', 'https://www.linkedin.com/in/example', 1);
