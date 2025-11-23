# Enkel Hotanalys för Rate Your Record (Backend)

Detta dokument beskriver de mest relevanta säkerhetsriskerna för backend-tjänsten, baserat på OWASP Top 10.

## Tillitsgränser (Trust Boundaries)

- **Användarens webbläsare -> Backend API:** All inkommande data från användare är opålitlig.
- **Backend API -> Discogs API:** Svaren från Discogs API är externa och måste hanteras som potentiellt opålitliga eller oväntade.
- **Backend API -> Databas:** Interaktionen med databasen är en kritisk punkt för dataintegritet och säkerhet.

## Topp 3 Relevanta Risker (OWASP Top 10)

### 1. A01:2021 - Broken Access Control

- **Risk:** En användare kan komma åt eller modifiera data som inte tillhör dem (t.ex. ändra någon annans betyg).
- **Kontroll:**
    - **Implementation:** Applikationen måste verifiera att den inloggade användaren (via t.ex. Spring Security Principal) äger den resurs som hen försöker modifiera.
    - **Status:** Behöver implementeras när användarhantering läggs till.

### 2. A03:2021 - Injection

- **Risk:** Skadlig data skickas till applikationen som exekveras oavsiktligt, oftast som en SQL-injektion.
- **Kontroll:**
    - **Implementation:** Använd Spring Data JPA med `JpaRepository`. Detta använder parameteriserade queries (Prepared Statements) under huven, vilket effektivt förhindrar SQL-injektioner.
    - **Status:** **Implementerat.**

### 3. A05:2021 - Security Misconfiguration

- **Risk:** Felaktig konfiguration av ramverk, server eller headers som exponerar applikationen för attacker.
- **Kontroll:**
    - **CORS:** Konfigurera `WebMvcConfigurer` för att endast tillåta anrop från den kända frontend-domänen.
    - **Säkerhetsheaders:** Använd ett bibliotek som Spring Security för att automatiskt lägga till skyddande headers (X-Content-Type-Options, X-Frame-Options etc.).
    - **Felmeddelanden:** Se till att applikationen inte läcker känslig information (t.ex. stack traces) i produktionsmiljö. Spring Boot hanterar detta väl som standard.
    - **Status:** Delvis implementerat. Kräver specifik CORS-konfiguration.
