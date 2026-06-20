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
