-- Simula Supabase: su esquema "public" nunca está vacío porque la plataforma
-- crea ahí sus propios objetos. Con "public" no vacío, Flyway se negaría a migrar
-- si la aplicación usara ese esquema.
--
-- Se ejecuta al crear la base de datos tanto en compose.yaml como en los tests
-- con Testcontainers (TestcontainersConfiguration).
CREATE TABLE public.supabase_platform_object (
    id INTEGER PRIMARY KEY
);
