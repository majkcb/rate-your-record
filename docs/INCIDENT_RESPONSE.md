# Enkel Incidentplan för Rate Your Record

Detta dokument beskriver stegen som ska tas om en allvarlig säkerhetsincident upptäcks i produktion.

## Kontaktvägar

- **Ansvarig utvecklare:** [Ditt Namn]
- **Kontakt:** [Din E-post / Telefon]

## Steg vid Incident

### 1. Identifiera & Verifiera

- **Vad:** Försök att snabbt verifiera att incidenten är verklig och förstå dess omedelbara påverkan.
- **Hur:**
    - Analysera loggar (applikationsloggar, serverloggar).
    - Försök att replikera problemet i en testmiljö om möjligt.
    - Kontrollera om data har blivit korrupt eller läckt.

### 2. Innesluta (Contain)

- **Vad:** Stoppa den omedelbara blödningen. Målet är att förhindra ytterligare skada.
- **Hur:**
    - **Stäng av åtkomst:** Om en specifik funktion missbrukas, överväg att tillfälligt stänga av den via en feature flag eller en snabb release.
    - **Blockera IP:** Om attacken kommer från en specifik IP-adress, blockera den på brandväggsnivå.
    - **Ta ner tjänsten:** I ett absolut nödfall, ta ner hela applikationen för att förhindra katastrofal dataförlust eller exponering.

### 3. Åtgärda & Återställa

- **Vad:** Utveckla och driftsätt en permanent lösning på sårbarheten.
- **Hur:**
    - Skriv kod för att åtgärda buggen.
    - Skriv ett test som verifierar att sårbarheten är borta.
    - Driftsätt fixen så snabbt som möjligt.
    - Om data har korrumperats, återställ från senaste kända fungerande backup.

### 4. Post-Mortem & Kommunikation

- **Vad:** Analysera vad som hände, varför det hände, och hur det kan förhindras i framtiden.
- **Hur:**
    - Skriv en rapport som sammanfattar incidenten.
    - Identifiera grundorsaken (root cause).
    - Uppdatera processer, kod eller övervakning för att förhindra att samma sak händer igen.
    - Kommunicera till eventuella påverkade användare (om personlig data har läckt).
