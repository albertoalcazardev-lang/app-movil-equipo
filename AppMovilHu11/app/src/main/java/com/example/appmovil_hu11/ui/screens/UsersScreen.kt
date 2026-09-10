package com.example.appmovil_hu11.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovil_hu11.data.User
import com.example.appmovil_hu11.ui.UserUiState
import com.example.appmovil_hu11.ui.UserViewModel
import com.example.appmovil_hu11.ui.theme.*

@Composable
fun UsersScreen(
    userRole: String = "Auditor",
    viewModel: UserViewModel = viewModel()
) {
    if (userRole == "Cliente") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Acceso Restringido", color = NovaAccion, fontWeight = FontWeight.Bold)
        }
        return
    }

    Scaffold(containerColor = NovaFondo) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text("Usuarios registrados", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NovaTexto)
            Text("Directorio de cuentas", fontSize = 14.sp, color = NovaTexto.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .background(NovaApoyo, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Solo lectura", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NovaConfianza)
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = viewModel.uiState) {
                is UserUiState.Loading -> LoadingUsersView()
                is UserUiState.Success -> UsersListView(users = state.users)
                is UserUiState.Error -> ErrorUsersView(onRetry = { viewModel.fetchUsers() })
            }
        }
    }
}

@Composable
fun UsersListView(users: List<User>) {
    Text("${users.size} cuentas", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NovaTexto, modifier = Modifier.padding(bottom = 12.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(users) { user -> UserCard(user = user) }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(NovaApoyo, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name.initials, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NovaConfianza)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(user.name.fullName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NovaTexto)
                Text("Usuario #${user.id}  @${user.username}", fontSize = 13.sp, color = NovaTexto.copy(alpha = 0.7f))
                Text(user.email, fontSize = 13.sp, color = NovaTexto.copy(alpha = 0.8f))
                Text(user.phone, fontSize = 13.sp, color = NovaTexto.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun LoadingUsersView() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NovaConfianza, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cargando usuarios...", color = NovaTexto, fontSize = 14.sp)
        }
        repeat(3) {
            Box(modifier = Modifier.fillMaxWidth().height(90.dp).padding(vertical = 6.dp).background(Color.White, shape = RoundedCornerShape(16.dp)))
        }
    }
}

@Composable
fun ErrorUsersView(onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("No pudimos cargar\nlos usuarios", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NovaTexto)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Ocurrió un problema de conexión. Puedes volver a intentarlo.", fontSize = 14.sp, color = NovaTexto.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = NovaAccion),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Reintentar", fontSize = 16.sp, color = Color.White)
        }
    }
}