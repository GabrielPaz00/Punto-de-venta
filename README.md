# Sistema Móvil de Gestión de Inventario y Punto de Venta

El presente proyecto es una solución móvil orientada a la administración eficiente de productos, niveles de stock, usuarios del sistema y la ejecución ágil de transacciones de venta comerciales. Cuenta con un control de accesos basado en roles para proteger los flujos operativos clave.

## 🛠 Tech Stack
* **Lenguaje:** Kotlin
* **Framework UI:** Jetpack Compose (Material Design 3)
* **Arquitectura:** MVVM (Model-View-ViewModel)
* **Backend as a Service (BaaS):** Firebase (Auth & Firestore)

---

## 🏗 Arquitectura del Sistema (Patrón MVVM)

La aplicación separa claramente la interfaz gráfica de la lógica de negocio y el acceso a datos.

### 1. Capa de Presentación (View)
Contiene las pantallas (`.kt`) que interactúan directamente con el usuario:
* **`LoginScreen.kt`**: Gestión de la interfaz de autenticación.
* **`ProfileScreen.kt`**: Visualización del perfil activo y cierre de sesión.
* **`UsersScreen.kt` + `UserForm`**: Interfaces de gestión (CRUD) de operadores.
* **`HomeScreen.kt`**: Panel principal con métricas financieras.
* **`ProductsScreen.kt` + `ProductForm`**: Catálogo y registro de artículos.
* **`PosScreen.kt` + `CartModal`**: Punto de Venta y carrito de compras.
* **`ReportsScreen.kt`**: Visualización detallada de reportes operativos.

### 2. Lógica de Presentación (ViewModels)
Intermediarios que procesan acciones y observan estados:
* **`LoginViewModel`**: Orquesta el inicio y cierre de sesión.
* **`ProfileViewModel`**: Administra la sesión activa.
* **`UsersViewModel`**: Lógica de gestión de roles y cuentas.
* **`HomeViewModel`**: Agregación de métricas para el Dashboard.
* **`ProductViewModel`**: CRUD de inventario y ajustes de stock.
* **`SaleViewModel`**: Estado del carrito y ejecución de la venta.

### 3. Capa de Datos (Modelos y Repositorios)
Centraliza la conexión con la nube para sincronización en tiempo real:
* **`AuthRepository`**: Conexión con Firebase Auth.
* **`UserRepository` (`User.kt`)**: Gestión de operadores en la colección "Usuarios" de Firestore.
* **`ProductRepository` (`Product.kt`)**: Catálogo físico y existencias en la colección "Productos".
* **`SaleRepository` (`Sale.kt`, `CartItem.kt`)**: Registro de transacciones en la colección "Ventas".

---

## ✨ Requerimientos Funcionales Implementados

* **Autenticación Segura (RF-01):** Inicio de sesión protegido con credenciales (correo y contraseña) y enmascaramiento de caracteres.
* **Gestión de Usuarios (RF-02):** Capacidad exclusiva del rol Administrador para buscar, añadir, modificar (Administrador / Usuario Estándar) y eliminar operadores.
* **Módulo POS (RF-03):** Selección de productos, agregación dinámica a un carrito y cálculo automatizado de Subtotales, IVA (16%) y Gran Total.
* **Control de Inventarios (RF-04):** Registro de nuevos artículos (con categoría, precio y stock inicial) y modificación incremental rápida (-10, +10) mediante ventanas emergentes.
* **Dashboard Informativo (RF-05):** Resumen financiero al instante con las ventas del día y el total de órdenes.

---

## 🚀 Flujo Operativo y UX

* **Interfaz de Usuario Inteligente (RNG-01):** Diseño moderno con modo oscuro nativo para mitigar la fatiga visual en entornos comerciales prolongados.
* **Sincronización Instantánea (RNG-02):** Los ingresos financieros diarios y las deducciones de stock se reflejan inmediatamente en la base de datos y en todos los módulos sin necesidad de recargar la pantalla manualmente.

---

## 👤 Autor

**Gabriel**
* GitHub: [@GabrielPaz00](https://github.com/GabrielPaz00)
