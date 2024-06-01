package views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import views.ParaTranzButtonTypes.*
import java.awt.Cursor

enum class ParaTranzButtonTypes{
    NORMAL, CLEAR, WARN, SUGGESTED
}

@Composable
fun ParaTranzButton(type: ParaTranzButtonTypes, imagePath: String = "", lable: String = "", enabled: Boolean = true, modifier: Modifier = Modifier, onclick: () -> Unit = {}, content: @Composable () -> Unit = {}) {
    Row(modifier = Modifier
        .then(modifier)
        .clip(AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
        .widthIn(min = 100.dp)
        .clickable(enabled = enabled) {
            onclick()
        }
        .then(
            if (enabled)
                when(type){
                    NORMAL -> Modifier.background(Color(0xfff1f1f1))
                    CLEAR -> Modifier.background(Color.White)
                    WARN -> Modifier.background(Color(0xffa1a1a1))
                    SUGGESTED -> Modifier.background(Color(0xff027BFF))
            } else
                Modifier.background(Color(0xfff7f7f7))
        )
        .then(
            if (enabled) Modifier
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
            else Modifier
        )
        .padding(horizontal = 14.dp, vertical = 12.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ){
        if (imagePath.isNotEmpty()){
            Image(painter = painterResource(imagePath), contentDescription = imagePath, modifier = Modifier.size(16.dp))
        }
        if (lable.isNotEmpty()){
            Text(lable, style = TextStyle(color =
            if (enabled)
                when(type){
                    NORMAL -> Color(0xff027BFF)
                    CLEAR -> Color(0xff027BFF)
                    WARN -> Color(0xff027BFF)
                    SUGGESTED -> Color(0xfff3f3f3)
            }
            else Color(0x70027BFF)
                , fontSize = 14.sp), textAlign = TextAlign.Center, maxLines = 1)
        }
        content()
    }
}