CREATE TABLE profile (
    id         SMALLINT      PRIMARY KEY,
    full_name  VARCHAR(100)  NOT NULL,
    headline   VARCHAR(150)  NOT NULL,
    summary    TEXT          NOT NULL,
    location   VARCHAR(100),
    avatar_url VARCHAR(2048),
    cv_url     VARCHAR(2048),
    -- Registro único: solo puede existir el perfil con id 1.
    CONSTRAINT profile_singleton CHECK (id = 1)
);

CREATE TABLE social_link (
    id         BIGINT        GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    profile_id SMALLINT      NOT NULL REFERENCES profile (id) ON DELETE CASCADE,
    platform   VARCHAR(20)   NOT NULL,
    url        VARCHAR(2048) NOT NULL,
    position   INTEGER       NOT NULL,
    CONSTRAINT social_link_platform_valid CHECK (platform IN (
        'github', 'gitlab', 'linkedin', 'x', 'bluesky',
        'mastodon', 'stackoverflow', 'website', 'email'
    )),
    CONSTRAINT social_link_position_non_negative CHECK (position >= 0),
    CONSTRAINT social_link_profile_position_unique UNIQUE (profile_id, position)
);
