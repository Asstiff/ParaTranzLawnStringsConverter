package views

import APIConfig
import APIConfig.showSettings
import Window.locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import languageSupport.LocalizationKey

@Composable
fun NoLayoutCalculationsComposable(
    width: Dp,
    height: Dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = { content() },
        modifier = Modifier.size(width, height)
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        layout(width.toPx().toInt(), height.toPx().toInt()) {
            placeable.place(0, 0)
        }
    }
}

@Composable
fun ParaTranzSettingsView(modifier: Modifier = Modifier) {
    val apiKey = remember { mutableStateOf("") }
    val projectId = remember { mutableStateOf("") }
    val fileName = remember { mutableStateOf("") }
    val showWarning1 = remember { mutableStateOf(false) }
    val showWarning2 = remember { mutableStateOf(false) }
    val showWarning3 = remember { mutableStateOf(false) }

    NoLayoutCalculationsComposable(width = 376.dp, height = 300.dp) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(300.dp)
                .requiredWidth(368.dp)
                .padding(top = 18.dp)
                .then(modifier)
        ) {
            Text("ParaTranz API", style = TextStyle(fontSize = 16.sp, color = Color(0xff707070)), fontWeight = FontWeight.Bold)
            Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), color = Color(0x20707070))
            LazyColumn {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ParaTranzInputView(label = locale.getString(LocalizationKey.INPUT_LABEL_API_KEY), string = apiKey, maxLength = 32, required = true,
                            warning = apiKey.value.length != 32 && showWarning1.value, showHint = showWarning1, hint = if (apiKey.value.isEmpty()) locale.getString(LocalizationKey.INPUT_HINT_NO_API_KEY) else locale.getString(LocalizationKey.INPUT_HINT_INVALID_API_KEY))
                        ParaTranzInputView(label = locale.getString(LocalizationKey.INPUT_LABEL_PROJECT_ID), string = projectId, required = true, customRegex = "^\\d+\$", maxLength = 6, warning = showWarning2.value, showHint = showWarning2, hint = locale.getString(LocalizationKey.INPUT_HINT_NO_PROJECT_ID))
                        ParaTranzInputView(label = locale.getString(LocalizationKey.INPUT_LABEL_FILE_NAME), string = fileName, required = true, maxLength = 128, warning = showWarning3.value, showHint = showWarning3, hint = locale.getString(LocalizationKey.INPUT_HINT_NO_FILE_NAME))
                    }
                }
            }
            if (showSettings.value){
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ParaTranzButton(type = ParaTranzButtonTypes.NORMAL, lable = locale.getString(LocalizationKey.BUTTON_LABEL_CANCEL), onclick = {
                        if (fileName.value.isEmpty()) {
                            fileName.value = APIConfig.fileName.value
                        }
                        if (projectId.value.isEmpty()) {
                            projectId.value = APIConfig.projectId.value
                        }
                        if (apiKey.value.isEmpty()) {
                            apiKey.value = APIConfig.apiKey.value
                        }
                        showSettings.value = false
                    })
                    ParaTranzButton(type = ParaTranzButtonTypes.SUGGESTED, lable = locale.getString(LocalizationKey.BUTTON_LABEL_DONE), onclick = {
                        if (apiKey.value.isNotEmpty() && apiKey.value.length == 32 && projectId.value.isNotEmpty() && fileName.value.isNotEmpty()){
                            APIConfig.fileName.value = fileName.value
                            APIConfig.projectId.value = projectId.value
                            APIConfig.apiKey.value = apiKey.value
                            showSettings.value = false
                        }
                        else {
                            if (apiKey.value.isEmpty() || apiKey.value.length != 32){
                                showWarning1.value = true
                            }
                            if (projectId.value.isEmpty()){
                                showWarning2.value = true
                            }
                            if (fileName.value.isEmpty()){
                                showWarning3.value = true
                            }
                        }
                    })
                }
            }
        }
    }
}