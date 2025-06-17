package com.example.trabalhofinal.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.screens.ItineraryBottomSheetUiState

/**
 * Conteúdo específico para exibir o roteiro de viagem gerado pelo Gemini em um Bottom Sheet.
 * Lida com os estados de carregamento, erro e exibição do roteiro.
 *
 * @param uiState O estado da UI contendo o roteiro, status de carregamento e erro.
 * @param onRetry Callback invocado quando o botão "Tentar Novamente" é clicado.
 * @param onExportRequest Callback invocado quando o botão "Exportar" é clicado, com o nome do arquivo sugerido.
 * (Observação: O botão de exportar em si será passado para o UniversalBottomSheetContent
 * pelo MainScreen, mas a lógica de chamada de callback para MainScreen permanece aqui)
 */
@Composable
fun TravelItineraryContent(
    uiState: ItineraryBottomSheetUiState,
    onRetry: () -> Unit,
    onExportRequest: (String) -> Unit
) {
    if (uiState.isLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Text(uiState.itineraryText)
    } else if (uiState.errorOccurred) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(uiState.itineraryText, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                Text("Tentar Novamente")
            }
        }
    } else {
        Text(text = uiState.itineraryText)
    }
}