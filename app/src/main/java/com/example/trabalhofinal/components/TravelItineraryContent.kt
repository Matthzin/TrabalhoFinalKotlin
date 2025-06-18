package com.example.trabalhofinal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.viewmodel.ItineraryBottomSheetUiState

fun removeMarkdownCharacters(text: String): String {
    var cleanedText = text

    // Remove negrito (**) - mantém apenas o texto interno
    cleanedText = cleanedText.replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
    // Remove itálico (*) - mantém apenas o texto interno
    cleanedText = cleanedText.replace(Regex("\\*(.*?)\\*"), "$1")

    // Remove cabeçalhos (#, ##, ###, etc.) - remove # e o espaço que segue
    // O modificador MULTILINE é essencial para que '^' corresponda ao início de cada linha.
    cleanedText = cleanedText.replace(Regex("^#+\\s", RegexOption.MULTILINE), "")

    // Remove itens de lista (* ou - no início da linha), sem deixar espaços no lugar
    cleanedText = cleanedText.replace(Regex("^[*-]\\s", RegexOption.MULTILINE), "") // <-- ALTERADO AQUI para ""

    // Remove blocos de código (```)
    cleanedText = cleanedText.replace("```", "")
    // Remove links ([texto](url)) - mantém apenas o texto visível
    cleanedText = cleanedText.replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")

    // Remove múltiplas quebras de linha (2 ou mais) e substitui por um único parágrafo novo.
    // Isso mantém a estrutura de parágrafos sem quebras de linha excessivas.
    cleanedText = cleanedText.replace(Regex("\\n{2,}"), "\n\n")

    // Etapa crucial: Processar cada linha para remover espaços no início
    val lines = cleanedText.split("\n")
    val processedLines = lines.map { it.trimStart() } // Remove espaços do início de CADA LINHA
    cleanedText = processedLines.joinToString("\n") // Junta as linhas de volta com as quebras originais

    return cleanedText.trim()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelItineraryContent(
    uiState: ItineraryBottomSheetUiState,
    onRetry: () -> Unit,
    onUserMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            item {
                if (uiState.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(uiState.itineraryText)
                } else if (uiState.errorOccurred) {
                    Text(uiState.itineraryText, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                        Text("Tentar Novamente")
                    }
                } else {
                    Text(text = removeMarkdownCharacters(uiState.itineraryText))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.userMessage,
                onValueChange = onUserMessageChange,
                label = { Text("Sugestão para o roteiro...") },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            )
            Button(
                onClick = onSendMessage,
                enabled = uiState.userMessage.isNotBlank() && !uiState.isLoading,
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar")
            }
        }
    }
}