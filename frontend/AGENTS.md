# AGENTS.md – Android-Frontend (Weather-App)

Du arbeitest NUR im Android-Teil dieser Übungsaufgabe.
Ziel: Jetpack-Compose-App mit MVVM, Navigation 3, REST-Anbindung ans Quarkus-Backend.

---

## Rolle des Agents

- Du bist Android-/Kotlin-Experte mit Fokus auf:
  - Jetpack Compose
  - Navigation 3 (typsichere Navigation)
  - MVVM + StateFlow
  - Retrofit ODER Ktor (einheitlich im Projekt)
- Halte dich an die Übungsanforderungen, schreibe kleine, fokussierte Änderungen.

---

## Build & Run

- App bauen (Debug):
  - `./gradlew assembleDebug`
- Unit-Tests:
  - `./gradlew testDebugUnitTest`
- IDE:
  - Verwende Android Studio Run-Konfigurationen für Emulator/Device.

Bitte:
- Keine komplexen Release-Builds ohne Nachfrage.
- Bei größeren Refactorings zuerst einen kurzen Plan vorschlagen.

---

## Architektur (MVVM, Repository, Navigation 3)

- Strikte Schichtung:
  - UI (Compose Screens) → ViewModel → Repository → REST-Client.
  - Composables rufen **niemals** direkt Retrofit/Ktor auf.
- ViewModels:
  - Mindestens:
    - `CityListViewModel` (Städteübersicht)
    - `CityDetailViewModel` (Wetterdetails)
- State-Kapselung:
  - Intern im ViewModel:
    - `private val _uiState = MutableStateFlow<UiState>(...)`
  - Extern für UI:
    - `val uiState: StateFlow<UiState> = _uiState.asStateFlow()`
    - Nutze `.asStateFlow()`.
  - Die UI darf keine Schreibrechte auf den Zustand haben.

---

## State in Compose

- In Screens:
  - State ausschließlich mit `collectAsStateWithLifecycle()` konsumieren.
  - Kein `remember { mutableStateOf(...) }` für Backend-Daten; diese kommen aus dem ViewModel.
- Modellierung:
  - UiState sollte Loading/Success/Error abbilden, z. B.
    - `data class CitiesUiState(val isLoading: Boolean, val error: String?, val cities: List<CityUiModel>)`.

---

## Navigation 3

- Verwende die neue **Navigation 3**-API mit typsicheren Routes/Arguments.
- Screens:
  - `CitiesScreen` (Liste aller Städte vom Backend).
  - `CityDetailScreen` (Details/Wetter für eine Stadt).
- Regeln:
  - Übergib nur, was nötig ist (z. B. `cityId` als Parameter).
  - Kein globaler Shared-State zum Navigieren.
  - Navigation muss lifecycle-freundlich sein (keine Leaks über statische Referenzen).

---

## REST-Client & Polling

- Verwende EINE Networking-Lösung im gesamten Projekt:
  - Retrofit ODER Ktor, nicht beides.
- Implementiere ein Repository, z. B. `WeatherRepository`:
  - Methoden: `getCities()`, `getWeather(cityId)`, etc.
  - Fehlerbehandlung (Exceptions → UiState.Error).
- Polling-Anforderung (Städteübersicht):
  - Alle 5 Sekunden automatisch aktualisieren:
    - z. B. in `CityListViewModel`:
      - Coroutine in `viewModelScope`, die alle 5 s `getCities()` aufruft.
    - Polling muss im Lifecycle verankert sein (Abbruch, wenn ViewModel zerstört).

---

## Fehlerbehandlung (Frontend)

- Wenn Backend nicht erreichbar ist:
  - Zeige klar erkennbare Fehlermeldung (Snackbar, Error-Screen mit Retry).
  - Lass die App nicht still „hängen“.
- UiState sollte Fehlerzustand klar transportieren (z. B. `errorMessage`).

---

## Bonus (optional): Room / UserData

- Lokale Persistenz mit Room:
  - Entity: `UserData(firstName, lastName, email)`.
  - DAO + Repository für CRUD.
- Formular-Screen:
  - Eingabe & Speichern von UserData.
- Integration:
  - Vorname (`firstName`) soll global in der Top-Bar jedes Screens angezeigt werden.
  - Top-Bar über Flow/StateFlow an UserData binden (reaktiv).

---

## Grenzen

**Erlaubt ohne Rückfrage:**

- Kleine Änderungen an ViewModels, Repositories und Screens.
- Ergänzen/Anpassen von UiState, Error-Handling und Navigation.

**Nur nach Rückfrage:**

- Einführung neuer Libraries (zusätzliche DI/Networking/State-Frameworks).
- Größere Reorganisation der Paketstruktur.

**Nie:**

- Backend-spezifische Logik (Temperaturvariation, JPA) im Android-Code implementieren.
- Sicherheitskritische Fake-Features einbauen (z. B. Dummy-Auth, die als „echt“ verkauft wird).
