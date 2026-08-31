# 💈 ManFar Barbershop - Kotlin Multiplatform (KMP) & Compose Multiplatform

Aplicación móvil moderna de gestión y reservas para barberías construida con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform**, permitiendo compartir lógica de negocio, modelos de dominio, componentes y vistas completas entre **Android** e **iOS**.

---

## 📁 Estructura del Proyecto (Árbol de Directorios)

```text
.
├── 📄 codemagic.yaml                        # Configuración de CI/CD para compilación iOS en la nube
├── 📄 build.gradle.kts                      # Configuración raíz de Gradle (plugins Kotlin, Compose, KSP, etc.)
├── 📄 settings.gradle.kts                   # Definición de módulos (:app y :shared) y repositorios
├── 📄 gradle.properties                    # Parámetros y optimizaciones de Gradle
├── 📄 metadata.json                         # Metadatos para AI Studio
├── 📄 README.md                             # Documentación general del proyecto
│
├── 📱 app/                                  # MÓDULO ANDROID (App nativa / punto de entrada Android)
│   ├── 📄 build.gradle.kts                  # Dependencias de Android (Room, Compose, Lifecycle, etc.)
│   └── src/
│       └── main/
│           ├── 📄 AndroidManifest.xml       # Declaración de permisos, tema y MainActivity
│           ├── java/com/example/
│           │   ├── 📄 MainActivity.kt       # Actividad principal, ViewModel de Android y enrutador
│           │   ├── data/                    # Capa de datos específica de Android (Persistencia local)
│           │   │   ├── local/               # Entidades y DAOs de Room Database
│           │   │   │   ├── BarbershopDatabase.kt
│           │   │   │   ├── AppointmentDao.kt
│           │   │   │   ├── ServiceDao.kt
│           │   │   │   ├── BarberDao.kt
│           │   │   │   ├── ClientUserDao.kt
│           │   │   │   └── BlockedSlotDao.kt
│           │   │   ├── mapper/              # Mapeadores entre entidades de base de datos y modelos compartidos
│           │   │   │   └── EntityMappers.kt
│           │   │   └── repository/          # Implementación de repositorios locales con Room
│           │   │       └── BarbershopRepository.kt
│           │   └── ui/
│           │       ├── screens/             # Pantallas del panel administrativo (Android)
│           │       │   ├── AdminDashboardScreen.kt
│           │       │   ├── AdminCalendarScreen.kt
│           │       │   ├── AdminServicesCrudScreen.kt
│           │       │   ├── AdminAvailabilityScreen.kt
│           │       │   ├── AdminClientsScreen.kt
│           │       │   └── UiHelpers.kt
│           │       └── theme/               # Tema visual oscuro con acentos dorados
│           │           ├── Color.kt
│           │           ├── Theme.kt
│           │           └── Type.kt
│           └── res/                         # Recursos de Android (Drawables, Strings, XMLs)
│               ├── values/
│               │   ├── colors.xml
│               │   ├── strings.xml
│               │   └── themes.xml
│               └── mipmap-*/                # Iconos de la aplicación
│
├── 🌐 shared/                               # MÓDULO MULTIPLATAFORMA (Lógica, UI compartida y modelos)
│   ├── 📄 build.gradle.kts                  # Configuración KMP y targets (Android + iOS XCFramework/Framework)
│   └── src/
│       ├── commonMain/kotlin/com/example/shared/
│       │   ├── 📄 Platform.kt               # Definición `expect` de plataforma
│       │   ├── logic/                       # Lógica pura de negocio (Cálculo de slots, filtros, analytics)
│       │   │   └── BarbershopCoreLogic.kt
│       │   ├── model/                       # Modelos de datos multiplatforma (Data classes)
│       │   │   └── BarbershopModels.kt
│       │   ├── repository/                  # Interfaces de repositorio compartidas
│       │   │   └── IBarbershopRepository.kt
│       │   ├── util/                        # Utilidades comunes (Formato de fechas, moneda, validaciones)
│       │   │   ├── DateTimeUtils.kt
│       │   │   └── FormatUtils.kt
│       │   └── ui/                          # Vistas y componentes en Compose Multiplatform
│       │       ├── 📄 SharedApp.kt          # Composable raíz compartido con navegación y estado
│       │       ├── screens/                 # Pantallas compartidas (Cliente y Auth)
│       │       │   ├── AuthScreen.kt
│       │       │   ├── ClientHomeScreen.kt
│       │       │   ├── BookingFlowScreen.kt
│       │       │   ├── ClientMyAppointmentsScreen.kt
│       │       │   └── ClientProfileScreen.kt
│       │       ├── components/              # Componentes reutilizables de UI
│       │       │   ├── AppointmentCard.kt
│       │       │   ├── ServiceCard.kt
│       │       │   ├── CustomCharts.kt
│       │       │   ├── StatusBadges.kt
│       │       │   ├── NotificationBanner.kt
│       │       │   └── NotificationsDialog.kt
│       │       └── theme/                   # Sistema de diseño compartido (Dark Obsidian & Gold)
│       │           ├── Color.kt
│       │           ├── Theme.kt
│       │           └── Type.kt
│       ├── androidMain/kotlin/com/example/shared/
│       │   └── 📄 Platform.kt               # Implementación `actual` para Android
│       └── iosMain/kotlin/com/example/shared/
│           ├── 📄 Platform.kt               # Implementación `actual` para iOS
│           └── 📄 MainViewController.kt     # Exporta ComposeUIViewController { SharedApp() } para iOS
│
└── 🍏 iosApp/                               # APLICACIÓN NATIVA IOS (SwiftUI Host)
    ├── 📄 iOSApp.swift                      # Punto de entrada principal SwiftUI (@main)
    ├── 📄 ContentView.swift                 # Contenedor UIViewControllerRepresentable que aloja Compose
    ├── 📄 Info.plist                        # Configuración y permisos de la aplicación iOS
    ├── 📁 Assets.xcassets/                  # Catálogo de recursos iOS (Iconos y colores)
    │   ├── 📄 Contents.json
    │   ├── 📁 AccentColor.colorset/
    │   └── 📁 AppIcon.appiconset/
    └── 📁 iosApp.xcodeproj/                # Proyecto Xcode configurado con el framework Kotlin
        └── 📄 project.pbxproj
```

---

## 🚀 Arquitectura y Tecnologías

1. **Kotlin Multiplatform (KMP)**:
   - Permite compartir código fuente común entre Android y iOS sin duplicar lógica de reservas, validaciones ni modelos de datos.
2. **Compose Multiplatform**:
   - Vistas declarativas compartidas (`AuthScreen`, `ClientHomeScreen`, `BookingFlowScreen`, etc.) renderizadas de manera nativa en ambas plataformas.
3. **Persistencia Local (Android)**:
   - **Room Database** con soporte para DAOs reactivos con Kotlin Coroutines y Flow.
4. **Interoperabilidad en iOS**:
   - `MainViewController.kt` en Kotlin expone la vista de Compose a través de UIKit (`ComposeUIViewController`).
   - SwiftUI consume el framework generado mediante `UIViewControllerRepresentable` en `ContentView.swift`.

---

## 🛠️ Instrucciones de Compilación

### 🤖 Android
Para compilar y ejecutar en Android:
```bash
./gradlew :app:assembleDebug
```

### 🍏 iOS (Local con Xcode)
1. Generar el framework compartido para simulador o dispositivo:
   ```bash
   ./gradlew :shared:embedAndSignAppleFrameworkForXcode
   ```
2. Abrir `iosApp/iosApp.xcodeproj` en **Xcode**.
3. Seleccionar el simulador o dispositivo destino y presionar **Run (Cmd + R)**.

### ☁️ CI/CD (Codemagic)
El proyecto incluye un archivo `codemagic.yaml` preconfigurado para compilar automáticamente el framework compartido y construir el artefacto `.app` de iOS sin requerir configuración manual.
