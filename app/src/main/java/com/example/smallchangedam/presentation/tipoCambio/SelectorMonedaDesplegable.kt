package com.example.smallchangedam.presentation.tipoCambio

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorMonedaDesplegable(
    label: String,
    seleccionada: String,
    monedas: Map<String, String>,
    onMonedaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = seleccionada,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            monedas.forEach { (codigo, nombre) ->
                DropdownMenuItem(
                    text = { Text(text = "$codigo - $nombre") },
                    onClick = {
                        onMonedaSeleccionada(codigo)
                        expandido = false
                    }
                )
            }
        }
    }
}