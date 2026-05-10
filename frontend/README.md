# WeatherApp — Android Frontend

A Jetpack Compose Android app for the Quarkus Weather backend.

## Architecture

```
app/
├── data/
│   ├── model/        # City, WeatherDetail data classes (Kotlinx Serializable)
│   ├── network/      # WeatherApiService (Retrofit), RetrofitClient
│   └── repository/   # WeatherRepository (sealed Result<T>)
├── navigation/       # Destination (type-safe, sealed class), WeatherNavGraph
├── ui/
│   ├── screens/      # CityListScreen, WeatherDetailScreen
│   └── theme/        # Theme.kt (dark color scheme)
└── viewmodel/        # CityListViewModel, WeatherDetailViewModel
```

## Key Design Decisions

- **MVVM**: UI ↔ ViewModel ↔ Repository ↔ Retrofit
- **State encapsulation**: `private MutableStateFlow` / `public val StateFlow = _x.asStateFlow()`
- **Navigation3**: Type-safe `Destination` sealed class, `rememberNavBackStack`, `NavDisplay`
- **Polling**: `CityListViewModel` polls every 5 seconds via a coroutine loop
- **Lifecycle-safe**: `collectAsStateWithLifecycle()` used in all screens
- **Error handling**: Snackbar on city list, full-screen error on detail with retry

## Setup

1. Open in Android Studio Ladybug (AGP 8.5+)
2. Set your backend URL in `RetrofitClient.kt`:
   - Android Emulator → localhost: `http://10.0.2.2:8080/`
   - Physical device: `http://<YOUR_PC_IP>:8080/`
3. Run the Quarkus backend first
4. Build & Run

## Backend API contract expected

```
GET /cities        → [{id, name, country}, ...]
GET /weather/{id}  → {cityId, cityName, temperature, description, humidity?, windSpeed?}
```

## Dependencies

- **Navigation3** `1.0.0-alpha03` — `androidx.navigation3:navigation3-ui`
- **Retrofit** `2.11.0` + Kotlinx Serialization converter
- **OkHttp** `4.12.0` with logging interceptor
- **Lifecycle** `2.8.6` — ViewModel, `collectAsStateWithLifecycle`
- **Compose BOM** `2024.09.03` + Material3
