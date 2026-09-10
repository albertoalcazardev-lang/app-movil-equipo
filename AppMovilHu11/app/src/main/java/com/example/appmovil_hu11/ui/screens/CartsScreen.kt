package com.example.appmovil_hu11.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovil_hu11.data.CartItemUI
import com.example.appmovil_hu11.data.CartUI
import com.example.appmovil_hu11.ui.CartUiState
import com.example.appmovil_hu11.ui.CartViewModel
import com.example.appmovil_hu11.ui.theme.*

@Composable
fun CartsScreen(
    userRole: String = "Auditor",
    viewModel: CartViewModel = viewModel()
) {
    if (userRole == "Cliente") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Acceso Restringido", color = NovaAccion, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
            Text("Histórico de Carritos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NovaTexto)
            Text("Monitoreo global de operaciones", fontSize = 14.sp, color = NovaTexto.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .background(NovaApoyo, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Modo Auditoría", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NovaConfianza)
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = viewModel.uiState) {
                is CartUiState.Loading -> LoadingCartsView()
                is CartUiState.Success -> CartsListView(carts = state.carts)
                is CartUiState.Error -> ErrorCartsView(onRetry = { viewModel.fetchCartsAndProducts() })
            }
        }
    }
}

@Composable
fun CartsListView(carts: List<CartUI>) {
    Text("${carts.size} carritos registrados", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NovaTexto, modifier = Modifier.padding(bottom = 12.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(carts) { cart -> CartCard(cart = cart) }
    }
}

@Composable
fun CartCard(cart: CartUI) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Carrito #${cart.id}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NovaTexto)
                    Text("Propietario: Usuario ID ${cart.userId}", fontSize = 13.sp, color = NovaTexto.copy(alpha = 0.7f))
                }
                Text(cart.date, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = NovaConfianza)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (expanded) "▲ Ocultar productos" else "▼ Ver desglose (${cart.items.size} artículos)",
                fontSize = 12.sp,
                color = NovaAccion,
                fontWeight = FontWeight.Bold
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = NovaApoyo, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    cart.items.forEach { item ->
                        CartItemRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItemUI) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.productTitle, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = NovaTexto)
            Text("ID Producto: ${item.productId}", fontSize = 11.sp, color = NovaTexto.copy(alpha = 0.6f))
        }
        Text("Cant: ${item.quantity}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NovaConfianza)
    }
}

@Composable
fun LoadingCartsView() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NovaConfianza, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cargando carritos globales...", color = NovaTexto, fontSize = 14.sp)
        }
        repeat(3) {
            Box(modifier = Modifier.fillMaxWidth().height(80.dp).padding(vertical = 6.dp).background(Color.White, shape = RoundedCornerShape(16.dp)))
        }
    }
}

@Composable
fun ErrorCartsView(onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Error al consultar el historial", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NovaTexto)
        Spacer(modifier = Modifier.height(8.dp))
        Text("No se pudo obtener la información de carritos.", fontSize = 14.sp, color = NovaTexto.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = NovaAccion),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Reintentar", color = Color.White)
        }
    }
}