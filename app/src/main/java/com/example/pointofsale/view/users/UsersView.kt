package com.example.pointofsale.view.users

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pointofsale.core.theme.PointOfSaleTheme
import com.example.pointofsale.core.components.Header
import com.example.pointofsale.core.components.CustomTextField
import com.example.pointofsale.model.entities.User
import com.example.pointofsale.viewmodel.users.UsersViewModel

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun UsersViewPreview() {
    PointOfSaleTheme(darkTheme = true) {
        UsersView()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun EditUserModalPreview() {
    PointOfSaleTheme(darkTheme = true) {
        EditUserModal(
            user = User(username = "Juan Perez", email = "juan@example.com", userLevel = "user"),
            onDismiss = {},
            onConfirm = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AddUserModalPreview() {
    PointOfSaleTheme(darkTheme = true) {
        AddUserModal(
            onDismiss = {},
            onConfirm = { _, _, _, _ -> }
        )
    }
}

@Composable
fun UsersView(viewModel: UsersViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentUser by viewModel.currentUserProfile.collectAsState()
    val context = LocalContext.current

    var userToEdit by remember { mutableStateOf<User?>(null) }
    var showAddUserModal by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isUpdateSuccess) {
        if (uiState.isUpdateSuccess) {
            val message = if (showAddUserModal) "Usuario creado con éxito" else "Rol actualizado con éxito"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateStatus()
            userToEdit = null
            showAddUserModal = false
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateStatus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Header(
            title = "Gestión de Usuarios",
            customSubtitle = "Administra los usuarios del sistema",
            icon = Icons.Default.PersonAdd,
            entityCount = uiState.usersList.size,
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
            onAddClick = { showAddUserModal = true }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading && uiState.usersList.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.usersList, key = { it.uid }) { user ->
                        val isCurrentUser = user.uid == currentUser?.uid
                        UserCard(
                            user = user,
                            isCurrentUser = isCurrentUser,
                            onClick = { 
                                if (!isCurrentUser) {
                                    userToEdit = user
                                } else {
                                    Toast.makeText(context, "No puedes editar tu propio rol", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    userToEdit?.let { user ->
        EditUserModal(
            user = user,
            onDismiss = { userToEdit = null },
            onConfirm = { newRole ->
                viewModel.updateUserRole(user, newRole)
            },
            onDelete = {
                viewModel.deleteUser(user)
            }
        )
    }

    if (showAddUserModal) {
        AddUserModal(
            onDismiss = { showAddUserModal = false },
            onConfirm = { username, email, password, role ->
                viewModel.createUser(username, email, password, role)
            }
        )
    }
}

@Composable
fun UserCard(user: User, isCurrentUser: Boolean = false, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isCurrentUser) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ) {
                Icon(
                    imageVector = if (isCurrentUser) Icons.Default.Person else Icons.Default.People,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (isCurrentUser) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(16.dp)
                        ) {}
                    }
                }
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Surface(
                color = if (isCurrentUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = user.userLevel.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AddUserModal(onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("user") }
    var passwordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    val isEmailValid = email.matches(emailRegex)
    val isButtonEnabled = username.isNotBlank() && email.isNotBlank() && isEmailValid && password.isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1A1A1A)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = "Agregar Nuevo Usuario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                    }
                }
                Text(
                    text = "Complete los datos del nuevo usuario",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    label = "Nombre Completo", 
                    value = username, 
                    onValueChange = { username = it }, 
                    placeholder = "Juan Pérez"
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    label = "Correo Electrónico", 
                    value = email, 
                    onValueChange = { email = it }, 
                    placeholder = "juan@ejemplo.com",
                    keyboardType = KeyboardType.Email,
                    isError = email.isNotEmpty() && !isEmailValid
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    label = "Contraseña", 
                    value = password, 
                    onValueChange = { password = it }, 
                    placeholder = "********",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Rol", 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = Color.Gray, 
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF252525)),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val roleText = if (role == "admin") "Administrador" else "Usuario Estándar"
                            Text(text = roleText, color = Color.White)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                        }
                    }
                    DropdownMenu(
                        expanded = expanded, 
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF252525))
                    ) {
                        listOf("admin", "user").forEach { r ->
                            val rText = if (r == "admin") "Administrador" else "Usuario Estándar"
                            DropdownMenuItem(
                                text = { Text(rText, color = Color.White) }, 
                                onClick = { role = r; expanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onConfirm(username, email, password, role) },
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBB86FC),
                        disabledContainerColor = Color(0xFFBB86FC).copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        "Agregar Usuario", 
                        fontWeight = FontWeight.Bold, 
                        color = if (isButtonEnabled) Color.Black else Color.Black.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun EditUserModal(user: User, onDismiss: () -> Unit, onConfirm: (String) -> Unit, onDelete: () -> Unit) {
    var role by remember { mutableStateOf(user.userLevel) }
    var expanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val roles = listOf("admin", "user")

    if (showDeleteConfirm) {
        DeleteUserConfirmDialog(
            username = user.username,
            onDismiss = { showDeleteConfirm = false },
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1A1A1A)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = "Editar Usuario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                    }
                }
                Text(
                    text = "Modifica los datos del usuario",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    label = "Nombre Completo", 
                    value = user.username, 
                    onValueChange = {}, 
                    placeholder = "Username",
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    label = "Correo Electrónico", 
                    value = user.email, 
                    onValueChange = {}, 
                    placeholder = "user@pos.com.mx",
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Rol", 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = Color.Gray, 
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF252525)),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val roleText = when(role) {
                                "admin" -> "Administrador"
                                "user" -> "Usuario Estándar"
                                else -> role
                            }
                            Text(text = roleText, color = Color.White)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                        }
                    }
                    DropdownMenu(
                        expanded = expanded, 
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF252525))
                    ) {
                        roles.forEach { r ->
                            val rText = if (r == "admin") "Administrador" else "Usuario Estándar"
                            DropdownMenuItem(
                                text = { Text(rText, color = Color.White) }, 
                                onClick = { role = r; expanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679))
                    ) {
                        Text("Eliminar", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { onConfirm(role) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
                    ) {
                        Text("Guardar Cambios", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteUserConfirmDialog(username: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1A1A1A)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Confirmar eliminación",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¿Está seguro de que desea eliminar al usuario $username? Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679))
                    ) {
                        Text("Eliminar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
