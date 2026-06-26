<h1 align="center">🌱 Ciudad Limpia</h1>

<p align="center">
Aplicación móvil para el reporte de incidencias ambientales y la participación ciudadana.
</p>

<p align="center">

![Android](https://img.shields.io/badge/Android-35-3DDC84?style=for-the-badge\&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=for-the-badge\&logo=kotlin)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge\&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge\&logo=mysql)

</p>

<h4 align="center">

**🌎 Reporta incidencias ambientales y ayuda a construir una ciudad más limpia y sostenible.**

</h4>

---

# 📑 Índice

* [📖 Descripción del Proyecto](#-descripción-del-proyecto)
* [🚀 Proyecto](#-proyecto)
* [📱 Características y Demostración](#-características-y-demostración)
* [🔨 Funcionalidades](#-funcionalidades)
* [🛠 Tecnologías Utilizadas](#-tecnologías-utilizadas)
* [🏗 Arquitectura](#-arquitectura)
* [📂 Estructura del Proyecto](#-estructura-del-proyecto)
* [🚀 Instalación](#-instalación)
* [📸 Capturas de Pantalla](#-capturas-de-pantalla)
* [👨‍💻 Autores](#-autores)
* [📄 Licencia](#-licencia)
* [⭐ Si te gustó este proyecto](#-si-te-gustó-este-proyecto)

---

# 📖 Descripción del Proyecto

**Ciudad Limpia** es una aplicación móvil desarrollada para fortalecer la comunicación entre los ciudadanos y las municipalidades mediante el reporte de incidencias ambientales.

Los usuarios pueden reportar basura acumulada, desmontes, áreas verdes deterioradas y otras incidencias utilizando fotografías y ubicación GPS en tiempo real.

Cada reporte es recibido por una plataforma administrativa que permite a la municipalidad organizar, atender y realizar seguimiento de cada incidencia de manera eficiente, mejorando la capacidad de respuesta y promoviendo una mayor participación ciudadana.

---

# 🚀 Proyecto

<p align="center">

## ✅ Proyecto

</p>

El proyecto cuenta con las siguientes funcionalidades implementadas:

* ✔ Sistema de autenticación.
* ✔ Registro e inicio de sesión.
* ✔ Reportes ambientales.
* ✔ Captura y carga de imágenes.
* ✔ Geolocalización.
* ✔ Panel principal.
* ✔ Perfil de usuario.
* ✔ Sistema de recompensas.
* ✔ Backend desarrollado con Spring Boot.

---

# 📱 Características y Demostración

La aplicación está diseñada para ser intuitiva y fácil de utilizar por cualquier ciudadano.

Con Ciudad Limpia los usuarios pueden:

* Crear una cuenta.
* Iniciar sesión.
* Reportar incidencias ambientales.
* Adjuntar fotografías como evidencia.
* Registrar automáticamente la ubicación del reporte.
* Consultar el estado de sus incidencias.
* Obtener puntos e insignias por colaborar con la comunidad.

<p align="center">
<img src="docs/images/app-preview.png" width="100%" alt="Vista previa de Ciudad Limpia">
</p>

---

# 🔨 Funcionalidades

### 📍 Reporte de incidencias

Permite registrar incidencias ambientales indicando una descripción, fotografía y ubicación GPS.

---

### 📷 Captura de fotografías

El usuario puede utilizar la cámara del dispositivo o seleccionar imágenes desde la galería para adjuntar evidencia.

---

### 📌 Geolocalización

Cada reporte almacena automáticamente la ubicación exacta donde ocurrió la incidencia.

---

### 🏆 Sistema de recompensas

Los ciudadanos obtienen puntos e insignias por cada reporte realizado, incentivando la participación.

---

### 👤 Perfil de usuario

Consulta de información personal, estadísticas, puntos acumulados y progreso dentro de la aplicación.

---

### 🏛 Panel administrativo

La municipalidad puede gestionar reportes, actualizar estados y realizar el seguimiento de cada incidencia.

---

# 🛠 Tecnologías Utilizadas

## 📱 Frontend

* Kotlin
* Android Studio
* ViewBinding
* RecyclerView
* Fragments

## ⚙ Backend

* Spring Boot
* Spring Security
* JPA / Hibernate

## 🗄 Base de Datos

* MySQL

---

# 🏗 Arquitectura

```text
                 Android App
                      │
                      ▼
                 REST API
                      │
                      ▼
                Spring Boot
                      │
                      ▼
                   MySQL
                      
             
```

---

# 📂 Estructura del Proyecto

```text
Ciudad-Limpia
│
├── AndroidStudio/
│   ├── app/
│   ├── gradle/
│   └── build.gradle.kts
│
├── SpringBoot/
│   ├── src/
│   ├── pom.xml
│   └── application.properties
│
├── docs/
│   └── images/
│       ├── banner.png
│       ├── app-preview.png
│       ├── login.png
│       ├── home.png
│       └── profile.png
│
└── README.md
```

---

# 🚀 Instalación

## 1. Clonar el repositorio

```bash
git clone https://github.com/usuario/Ciudad-Limpia.git
```

## 2. Abrir el proyecto

Abrir el proyecto utilizando **Android Studio**.

## 3. Configurar Firebase

Agregar el archivo:

```text
google-services.json
```

dentro de:

```text
app/
```

## 4. Configurar la API

Modificar la URL del backend.

```kotlin
const val BASE_URL = "http://10.0.2.2:8080/"
```

## 5. Ejecutar

Sincronizar Gradle y ejecutar la aplicación desde Android Studio.

## 6. Mejor aún instalar el apk(usuario)

Instalar el archivo app-debug-androidTest.apk en su telefono para empezar a usar la app y contribuir con nuestra ciudad.

---

# 📸 Capturas de Pantalla

A continuación se muestran algunas de las principales pantallas de la aplicación.

<p align="center">

<img src="docs/images/app-preview.png" width="100%" alt="Capturas Ciudad Limpia">

</p>

| Pantalla            | Descripción                                    |
| ------------------- | ---------------------------------------------- |
| 🚀 Onboarding       | Introducción a la aplicación.                  |
| 🔐 Inicio de sesión | Acceso seguro mediante autenticación.          |
| 🏠 Inicio           | Panel principal del usuario.                   |
| 👤 Perfil           | Información personal y sistema de recompensas. |

---

# 👨‍💻 Autores

## Nick Rodriguez, Diego Cabrera, Victor Carhuamaca

Estudiantes de **Computación e Informática** en **Cibertec**.

Proyecto desarrollado con fines académicos como propuesta de transformación digital para la gestión de incidencias ambientales municipales.

---

# 📄 Licencia

Este proyecto fue desarrollado con fines educativos y académicos. Puede ser utilizado como referencia para proyectos de aprendizaje y desarrollo de aplicaciones móviles.

---

# ⭐ Si te gustó este proyecto

Si este proyecto te resultó interesante, considera darle una **⭐** al repositorio para apoyar su desarrollo y facilitar que más personas puedan conocerlo.
