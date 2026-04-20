# AGENTS.md – Backend (Quarkus-Server)

Du arbeitest NUR im Quarkus-Backend dieser Übungsaufgabe.
Ziel: Wetter-API als Datenquelle für die Android-App (Städte + dynamische Temperaturen).

---

## Rolle des Agents

- Du bist Java-/Quarkus-/JPA-Experte.
- Fokus:
  - Quarkus RESTEasy Reactive
  - Hibernate ORM mit Panache / JPA
  - Datenbank-Initialisierung mit Testdaten
  - Sinnvolle REST-Responses und Fehlercodes
- Halte dich an die Aufgabenbeschreibung, liefere kleine, nachvollziehbare Änderungen.

---

## Build & Run

- Schneller Build ohne Tests:
  - `./mvnw clean install -DskipTests`
- Tests:
  - `./mvnw test`
- Dev-Modus:
  - `./mvnw quarkus:dev`

Bitte:
- Keine lang laufenden CI-/Release-Builds ohne ausdrückliche Anweisung.
- Bei größeren Strukturänderungen zuerst einen kurzen Plan skizzieren.

---

## API-Anforderungen

Bereitstelle mindestens folgende Endpunkte:

- `GET /cities`
  - Liefert Liste aller gespeicherten Städte (z. B. `id`, `name`, optional `country`).
- `GET /weather/{cityId}`
  - Liefert Wetterdetails für die Stadt mit dieser ID:
    - Temperatur (in °C, dynamisch)
    - Beschreibung (z. B. „sonnig“, „bewölkt“)

### Temperatur-Dynamik

- Jede Stadt hat einen **persistenten Basiswert** in der DB (z. B. `baseTemperature`).
- Bei jedem Request auf `/weather/{cityId}`:
  - Berechne Temperatur als `baseTemperature ± 1°C` zufällig.
  - Ziel: leichte Variation bei jedem Abruf, kein reines Fix-Value.

---

## Persistenz (JPA, Panache)

- Nutze Hibernate ORM mit Panache:
  - Entweder `PanacheEntity` oder `PanacheRepository`.
- Entities:
  - `City`:
    - `id` (Primary Key)
    - `name`
    - optional `country`
    - `baseTemperature`
- Optional eigene DTOs für API-Rückgaben, um Entities nicht direkt zu exponieren.

### Schichtentrennung

- Empfohlenes Pattern:
  - Resource (REST) → Service → Repository/Entity.
- Regeln:
  - REST-Ressourcen enthalten keine komplexe Geschäftslogik.
  - Datenbankzugriffe in Repositories/Services bündeln.
  - `@Transactional` nur auf Service-Methoden, nicht direkt auf REST-Endpunkten.

---

## Initialdaten / Seeding

- Beim Start sollen beispielhafte Städte automatisch angelegt werden:
  - Mindestens: **Berlin**, **Wien**, **Zürich**.
- Implementierungsmöglichkeiten:
  - Startup-Bean (z. B. mit `@Startup` / `@Singleton` und `@PostConstruct`).
  - oder ein Initializer, der im Dev-/Test-Profil läuft.
- Achte darauf, dass mehrfaches Starten nicht zu doppelten Einträgen führt
  (z. B. `findByName` + nur anlegen, wenn nicht vorhanden).

---

## Fehlerbehandlung & HTTP-Codes

- `GET /weather/{cityId}`:
  - Wenn Stadt nicht existiert → `404 Not Found`.
- Unerwartete Fehler:
  - 500, aber keine Stacktraces im Response-Body.
- Liefere klare, einfache JSON-Strukturen, die leicht vom Android-Client konsumiert werden können.

---

## Zusammenarbeit mit Android-Client

- API ist für eine Android-App mit Polling gedacht:
  - Optimiere Responses auf Einfachheit (keine überkomplexen Objektgraphen).
- Änderungen an Endpoint-Signaturen:
  - Nur vornehmen, wenn der Contract klar ist; bei Breaking Changes klar dokumentieren.

---

## Grenzen

**Erlaubt ohne Rückfrage:**

- Ergänzen/Verbessern von REST-Ressourcen, Services und Repositories.
- Hinzufügen sinnvoller Tests (`@QuarkusTest`) für Endpunkte und Services.

**Nur nach Rückfrage:**

- DB-Schema größer umstrukturieren (z. B. neue Tabellen, Deep-Refactor von Entities).
- Neue externe Abhängigkeiten hinzufügen (z. B. weitere Datenbanken, Messaging-Systeme).

**Nie:**

- Harte Kodierung von Credentials/Secrets in Properties oder Code.
- Entfernen der Temperatur-Dynamik oder des Datenbank-Seeding-Mechanismus.
