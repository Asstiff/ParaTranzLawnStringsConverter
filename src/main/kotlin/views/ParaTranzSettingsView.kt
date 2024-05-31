package views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
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
fun ParaTranzSettingsView() {
    val apiKey = remember { mutableStateOf("") }

    NoLayoutCalculationsComposable(width = 320.dp, height = 372.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.size(width = 300.dp, height = 400.dp)) {
            Text("ParaTranz API", style = TextStyle(fontSize = 16.sp, color = Color(0xff707070)), fontWeight = FontWeight.Bold)
            Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), color = Color(0x20707070))
            LazyColumn {
                item {
                    Row(modifier = Modifier
                        .background(
                            Color(0xfff1f1f1),
                            shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("API Key", style = TextStyle(color = Color(0xff717171), fontWeight = FontWeight.Normal))
                        BasicTextField(
                            value = apiKey.value,
                            textStyle = TextStyle(color = Color(0xff414141), fontWeight = FontWeight.Bold),
                            onValueChange = {
                                if (it.length <= 32){
                                    apiKey.value = it
                                }
                                            },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}