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
* **`AuthViewModel`**: Orquesta el inicio y cierre de sesión.
* **`ProfileViewModel`**: Administra la sesión activa.
* **`UserViewModel`**: Lógica de gestión de roles y cuentas.
* **`HomeViewModel`**: Agregación de métricas para el Dashboard.
* **`ProductViewModel`**: CRUD de inventario y ajustes de stock.
* **`PosViewModel`**: Estado del carrito y ejecución de la venta.

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

* [cite_start]**Interfaz de Usuario Inteligente (RNG-01):** Diseño moderno con modo oscuro nativo para mitigar la fatiga visual en entornos comerciales prolongados[cite: 75].
* [cite_start]**Sincronización Instantánea (RNG-02):** Los ingresos financieros diarios y las deducciones de stock se reflejan inmediatamente en la base de datos y en todos los módulos sin necesidad de recargar la pantalla manualmente[cite: 64, 76].

---

## 👤 Autor

**Gabriel**
* GitHub: [@GabrielPaz00](https://github.com/GabrielPaz00)


# Plan de Pruebas Unitarias
Basado en los requerimientos y casos de uso del Sistema Móvil de Gestión de Inventario y POS.

### CU-01 - Autenticación en el Sistema (Login)

| IDENTIFICADOR | TC-UNIT-01 |
| :--- | :--- |
| **DESCRIPCIÓN** | Autenticación exitosa de usuario administrador. |
| **PRECONDICIONES** | `AuthViewModel` instanciado; Repositorio mockeado para retornar éxito con rol Admin. |
| **DATOS DE ENTRADA** | correo: "admin@empresa.com", password: "password123" |
| **PASOS A SEGUIR** | 1. InvocaronEmailChange("admin@empresa.com")<br>2. Invocar onPasswordChange("password123")<br>3. Ejecutar función `login()` |
| **RESULTADO ESPERADO** | `authState`: Success; Redirección a Dashboard principal; Rol validado como Administrador. |

### CU-02 - Registro de Nuevo Usuario

| IDENTIFICADOR | TC-UNIT-02 |
| :--- | :--- |
| **DESCRIPCIÓN** | Creación exitosa de un usuario estándar por parte del administrador. |
| **PRECONDICIONES** | `UserManagementViewModel` instanciado; Usuario actual con privilegios de Administrador. |
| **DATOS DE ENTRADA** | nombre: "Juan Perez", correo: "juan@empresa.com", pass: "12345", rol: "Usuario Estándar" |
| **PASOS A SEGUIR** | 1. Ingresar datos en el formulario (nombre, correo, contraseña)<br>2. Seleccionar rol "Usuario Estándar"<br>3. Invocar función `addUser()` |
| **RESULTADO ESPERADO** | El mock del repositorio es llamado exactamente 1 vez; `userListState` se actualiza con el nuevo usuario. |

### CU-03 - Modificación de Existencias (Stock)

| IDENTIFICADOR | TC-UNIT-03 |
| :--- | :--- |
| **DESCRIPCIÓN** | Actualización rápida de stock utilizando controles de adición (+10). |
| **PRECONDICIONES** | `InventoryViewModel` instanciado; Producto existente en catálogo con stock 50. |
| **DATOS DE ENTRADA** | productId: "PROD-001", incremento: 10 |
| **PASOS A SEGUIR** | 1. Seleccionar producto en la UI<br>2. Invocar `updateStockQuick("PROD-001", 10)` |
| **RESULTADO ESPERADO** | El repositorio actualiza el stock a 60; `productStockState` refleja el valor 60 inmediatamente. |

### CU-04 - Ejecución de Venta (Módulo POS)

| IDENTIFICADOR | TC-UNIT-04 |
| :--- | :--- |
| **DESCRIPCIÓN** | Cálculo correcto de subtotales, IVA y total en el carrito de compras. |
| **PRECONDICIONES** | `POSViewModel` instanciado; Carrito vacío. |
| **DATOS DE ENTRADA** | Producto A (precio: 100.0, cant: 2), Producto B (precio: 50.0, cant: 1) |
| **PASOS A SEGUIR** | 1. Invocar `addToCart(Producto A, 2)`<br>2. Invocar `addToCart(Producto B, 1)`<br>3. Observar estados calculados |
| **RESULTADO ESPERADO** | `subtotal`: 250.00; `iva`: 40.00; `granTotal`: 290.00. |

### CU-05 - Alta de Nuevo Producto

| IDENTIFICADOR | TC-UNIT-05 |
| :--- | :--- |
| **DESCRIPCIÓN** | Registro de un nuevo producto en el catálogo. |
| **PRECONDICIONES** | `InventoryViewModel` instanciado; Usuario con perfil de gestión superior. |
| **DATOS DE ENTRADA** | nombre: "Teclado Mecánico", precio: 1200.0, stock: 15, categoría: "Teclado" |
| **PASOS A SEGUIR** | 1. Llenar campos de formulario de producto<br>2. Invocar función `addNewProduct()` |
| **RESULTADO ESPERADO** | Repositorio invocado con los datos correctos; Estado de éxito emitido a la vista. |

# Matriz de Pruebas de Integración

Casos de prueba (test cases) para evaluar la sincronización entre módulos (Módulo POS, Inventario y Dashboard).

### Integración: Ejecución de Venta y Sincronización de Módulos

| IDENTIFICADOR | TC-INT-01 |
| :--- | :--- |
| **DESCRIPCIÓN** | Ejecución de venta exitosa descuenta inventario y actualiza métricas financieras (RNG-02). |
| **PRECONDICIONES** | `ExecuteSaleUseCase` instanciado; Repositorios de Inventario y Ventas conectados a BD local; Producto existente con stock inicial de 10. |
| **DATOS DE ENTRADA** | productId: "PROD-MONITOR", quantity: 2, price: 3000.0 |
| **PASOS A SEGUIR** | 1. Registrar producto en `InventoryRepository` (stock: 10)<br>2. Crear lista `cartItems` con 2 unidades del producto.<br>3. Invocar `executeSaleUseCase(cartItems)` |
| **RESULTADO ESPERADO** | `result`: Success; `updatedProduct.stock`: 8; `dailySummary.totalRevenue`: 6960.00; `dailySummary.totalOrders`: 1 |

### Integración: Validación de Stock Insuficiente

| IDENTIFICADOR | TC-INT-02 |
| :--- | :--- |
| **DESCRIPCIÓN** | Bloqueo de venta y rollback si la cantidad solicitada supera el stock físico. |
| **PRECONDICIONES** | `ExecuteSaleUseCase` instanciado; Producto con stock de 5. |
| **DATOS DE ENTRADA** | productId: "PROD-MOUSE", quantity: 10, price: 250.0 |
| **PASOS A SEGUIR** | 1. Registrar producto (stock: 5)<br>2. Intentar venta con 10 unidades.<br>3. Invocar `executeSaleUseCase(cartItems)` |
| **RESULTADO ESPERADO** | `result`: Failure (OutOfStockException); `product.stock` permanece en 5; `dailySummary` no se altera. |
