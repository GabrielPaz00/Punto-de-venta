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

* **Interfaz de Usuario Inteligente (RNG-01):** Diseño moderno con modo oscuro nativo para mitigar la fatiga visual en entornos comerciales prolongados.
* **Sincronización Instantánea (RNG-02):** Los ingresos financieros diarios y las deducciones de stock se reflejan inmediatamente en la base de datos y en todos los módulos sin necesidad de recargar la pantalla manualmente.

---

## 👤 Autor

**Gabriel**
* GitHub: [@GabrielPaz00](https://github.com/GabrielPaz00)


# Plan de Pruebas Unitarias
Basado en los requerimientos y casos de uso del Sistema Móvil de Gestión de Inventario y POS.

### CU-01 - Autenticación en el Sistema (Login)

 IDENTIFICADOR | TC-UNIT-01 |
 :--- | :--- |
 **DESCRIPCIÓN** | Autenticación exitosa de usuario. |
 **PRECONDICIONES** | `LoginViewModel` instanciado; Repositorio mockeado para retornar éxito. |
 **DATOS DE ENTRADA** | email: "admin@institucional.com", password: "password123" |
 **PASOS A SEGUIR** | 1. Invocar `login("admin@institucional.com", "password123")` |
 **RESULTADO ESPERADO** | `uiState.isAuthenticated`: true; `uiState.userProfile`: No nulo; `errorMessage`: nulo. |

### CU-02 - Registro de Nuevo Usuario

 IDENTIFICADOR | TC-UNIT-02 |
 :--- | :--- |
 **DESCRIPCIÓN** | Creación exitosa de un usuario por parte del administrador. |
 **PRECONDICIONES** | `UsersViewModel` instanciado; Usuario actual con privilegios de Administrador. |
 **DATOS DE ENTRADA** | nombre: "Juan Perez", correo: "juan@empresa.com", pass: "12345", rol: "Usuario Estándar" |
 **PASOS A SEGUIR** | 1. Invocar función `createUser("Juan Perez", "juan@empresa.com", "12345", "Usuario Estándar")` |
 **RESULTADO ESPERADO** | El mock del repositorio es llamado; `uiState.isUpdateSuccess` es true. |

### CU-03 - Modificación de Existencias (Stock)

 IDENTIFICADOR | TC-UNIT-03 |
 :--- | :--- |
 **DESCRIPCIÓN** | Actualización de stock de un producto. |
 **PRECONDICIONES** | `ProductViewModel` instanciado; Producto existente con ID "prod123". |
 **DATOS DE ENTRADA** | productId: "prod123", newStock: 20 |
 **PASOS A SEGUIR** | 1. Invocar `updateStock("prod123", 20)` |
 **RESULTADO ESPERADO** | El repositorio recibe la actualización; Se llama a `productRepository.updateStock`. |

### CU-04 - Ejecución de Venta (Módulo POS)

 IDENTIFICADOR | TC-UNIT-04                                                                                                       |
 :--- |:-----------------------------------------------------------------------------------------------------------------|
 **DESCRIPCIÓN** | Cálculo correcto de Total, IVA (16%) y Subtotal en el carrito.                                            |
 **PRECONDICIONES** | `SaleViewModel` instanciado; Carrito vacío.                                                                       |
 **DATOS DE ENTRADA** | Producto A (precio: 100.0, cant: 2), Producto B (precio: 50.0, cant: 1)                                          |
 **PASOS A SEGUIR** | 1. Invocar `addToCart(Producto A)` x2<br>2. Invocar `addToCart(Producto B)`<br>3. Observar estados calculados |
 **RESULTADO ESPERADO** | `total`: 250.00; `iva` (16%): 40.00; `subtotal` (Total - IVA): 210.00.                                                           |

### CU-05 - Alta de Nuevo Producto

 IDENTIFICADOR | TC-UNIT-05 |
 :--- | :--- |
 **DESCRIPCIÓN** | Registro de un nuevo producto en el catálogo. |
 **PRECONDICIONES** | `ProductViewModel` instanciado. |
 **DATOS DE ENTRADA** | Product(name: "Teclado Mecánico", price: 1200.0, stock: 15, category: "Teclado") |
 **PASOS A SEGUIR** | 1. Invocar función `createProduct(product)` |
 **RESULTADO ESPERADO** | Repositorio invocado con los datos correctos (`createProduct`). |

# Matriz de Pruebas de Integración

Casos de prueba para evaluar la sincronización entre módulos.

### Integración: Ejecución de Venta y Sincronización de Inventario

 IDENTIFICADOR | TC-INT-01 |
 :--- | :--- |
 **DESCRIPCIÓN** | Ejecución de venta exitosa descuenta inventario. |
 **PRECONDICIONES** | `SaleViewModel` y `ProductRepository` configurados. |
 **PASOS A SEGUIR** | 1. Realizar venta de un producto.<br>2. Verificar descuento en repositorio de productos. |
 **RESULTADO ESPERADO** | La venta se registra y el stock disminuye proporcionalmente. |
