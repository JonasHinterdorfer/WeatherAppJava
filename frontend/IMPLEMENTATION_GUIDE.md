# Implementierungsleitfaden: Weather-App (Android Frontend)

Dieser Leitfaden dokumentiert die Architektur und die getroffenen Designentscheidungen der Android Weather-App. Er dient als Vorbereitung für die Präsentation oder das Gespräch mit dem Lehrpersonal und adressiert direkt alle geforderten Bewertungsschwerpunkte der Übungsaufgabe.

---

## 1. Architektur-Übersicht: MVVM Muster

Die App wurde streng nach dem **Model-View-ViewModel (MVVM)** Design Pattern entwickelt. Dies garantiert eine saubere Trennung von UI (Compose Screens), Präsentationslogik (ViewModels) und Datenhaltung (Repository/REST/Room).

### Die 3 Schichten:
1. **View (UI Layer):** Jetpack Compose Screens (`CityListScreen`, `WeatherDetailScreen`, `UserSettingsScreen`). Die UI ist rein reaktiv und reagiert nur auf State-Änderungen.
2. **ViewModel (Präsentationslogik):** Zustandshaltung und Logik (`CityListViewModel`, `WeatherDetailViewModel`, `UserViewModel`). Bereitet die Daten für die View vor.
3. **Model / Data Layer:** Verantwortlich für die Beschaffung der Daten, entweder über das Netzwerk (`WeatherRepository` mithilfe von Retrofit) oder aus der lokalen Datenbank (`UserDatabase` mithilfe von Room).

---

## 2. State-Management & Encapsulation (WICHTIG!)

Ein zentraler Bewertungspunkt ist das State-Management in den ViewModels. Hier wurde strikt auf korrekte Datenkapselung (Encapsulation) geachtet.

*   **Der interne State (Privat & Mutable):**
    Im ViewModel wird der Zustand in einem privaten `MutableStateFlow` gehalten (z. B. `private val _uiState = MutableStateFlow(...)`). Dadurch kann **nur das ViewModel** die Daten verändern (etwa wenn Daten vom Repository erfolgreich geladen wurden).
*   **Der gebundene State für die UI (Public & Read-Only):**
    Über `val uiState: StateFlow<...> = _uiState.asStateFlow()` wird der Zustand für die UI exponiert. Die View-Schicht (die Composables) kann diesen State lesen, aber *nicht direkt modifizieren*.
*   **Lifecycle-Aware Collection:**
    In der UI wird der State mittels `collectAsStateWithLifecycle()` (oder standardmäßig `collectAsState()` in Compose gekoppelt mit Flow) konsumiert. Dies stellt sicher, dass State-Updates nur dann verarbeitet werden, wenn die App/der Screen auch im Vordergrund aktiv ist, was Ressourcen (Batterie, CPU) schont.

---

## 3. Navigation mit der neuen `Navigation3` Bibliothek

Der Navigationsfluss wurde vollständig typsicher mit der neuen Jetpack **Navigation3** API modelliert. Das spiegelt sich hauptsächlich in der Datei `WeatherNavGraph.kt` wider.

### Design-Entscheidungen:
*   **Typsicherheit:** Alle Destinations (`Destination.CityList`, `Destination.WeatherDetail`, `Destination.UserSettings`) sind als Kotlin-Klassen/Objekte abgebildet. Früher nutzte man fehleranfällige Strings für Routen, Navigation3 ermöglicht den Datenaustausch (z. B. `cityId`, `cityName` an die Detailansicht) nun über typsichere Datenstrukturen.
*   **`NavDisplay` & `backStack`:** Anstelle des klassischen `NavHost` wird das neue `NavDisplay` aus Navigation3 genutzt. Der Back-Stack wird manuell über `rememberNavBackStack` initialisiert und mittels `backStack.add()` befüllt, was ein deklaratives Navigationsmuster ermöglicht.
*   **Entry Decorators:** Durch `rememberViewModelStoreNavEntryDecorator()` bleiben ViewModels korrekt mit dem Screen-Lifecycle verknüpft (z.B. der DetailViewModel-Status bleibt beim Drehen des Geräts intakt).

---

## 4. Netzwerk-Kommunikation und Polling (Retrofit & Coroutines)

Die Verbindung zum Quarkus-Backend geschieht über Retrofit.

*   **Polling (Automatische Aktualisierung):**
    Um die dynamisch variierenden Temperaturen alle 5 Sekunden zu aktualisieren, läuft im `CityListViewModel` innerhalb des `viewModelScope` ein eigener Polling-Coroutine-Job. Dieser führt eine Kotlin Coroutine-Verzögerung `delay(5000)` aus und ruft wiederholt die Daten über das `WeatherRepository` ab.
*   **Fehlerbehandlung:** 
    Wenn das Backend nicht erreichbar ist, fängt das Repository die Exception ab und liefert einen Fehler-Zustand (Error-Message) zurück. Das ViewModel schreibt dies in den State (`errorMessage`), woraufhin die UI reaktiv reagiert und einen Fehler (z.B. Error-Text oder Snackbar) anzeigt.

---

## 5. Lokale Persistenz (Room DB) – Die Bonusaufgabe

Die Speicherung lokaler Benutzerdaten wurde mithilfe der **Room Database** umgesetzt.

*   **DataAccessObject (DAO):** In `UserDao.kt` und `UserDatabase.kt` wird eine SQLite-Datenbank abstrahiert, um `UserData` (Vorname, Nachname, E-Mail) persistent auf dem Gerät zu speichern.
*   **Reaktive UI Anbindung:** 
    Das Datenlesen in Room ist als `Flow` definiert: `fun getUserData(): Flow<UserData?>`. Im `UserViewModel` wird dieser Flow mittels `stateIn` in einen `StateFlow` umgewandelt. Das erlaubt es (vgl. `WeatherNavGraph.kt`), in der zentralen Top-Bar (Scaffold), den Namen ("Hello, [Vorname]") über *alle* Screens hinweg verzögerungsfrei zu aktualisieren.

---

## Argumentationshilfe für das Lehrergespräch (Zusammenfassung)

Wenn der Dozent/Lehrer fragt, wie die Kernanforderungen gelöst wurden, kannst du wie folgt antworten:

1.  *"Wie habt ihr Architektur & UI-Aktualisierung umgesetzt?"*
    > "Wir nutzen MVVM. Der Zustand liegt gekapselt in `StateFlows` (read-only für die UI, privat veränderbar im ViewModel). Jetpack Compose liest den State reaktiv aus. Wenn das Polling neue Daten vom Repository sendet, updatet das ViewModel den `MutableStateFlow` und Compose zeichnet den Screen automatisch neu."
2.  *"Was ist das Besondere an eurer Navigation?"*
    > "Wir haben die moderne Jetpack Navigation3 API integriert (siehe `WeatherNavGraph`). Sie erlaubt es uns komplett ohne Strings, sondern rein typsicher über Kotlin-Klassen (z. B. `Destination.WeatherDetail(id)`) zwischen Screens mitsamt Argumenten zu wechseln."
3.  *"Wie funktioniert das Polling und die API-Anbindung?"*
    > "Über Retrofit binden wir das Quarkus Backend an. Das regelmäßige Abrufen der Wetterdaten haben wir mit Kotlin Coroutines gelöst. Eine Schleife holt die Daten, pausiert durch `delay(5000)` für 5 Sekunden, ohne den Main-Thread (die UI) zu blockieren."
4.  *"Funktioniert die Bonusaufgabe mit der Datenbank?"*
    > "Ja, wir nutzen Androids Room Database für den angemeldeten Benutzer. Sobald etwas in die Datenbank geschrieben wird, schickt das System über einen Flow ein Event direkt bis in unsere TopAppBar in Compose hoch."

