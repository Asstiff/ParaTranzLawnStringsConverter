package views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ParaTranzInputView(label: String = "", placeholder: String = "", customRegex: String = "", string: MutableState<String>, required: Boolean = false, warning: Boolean = false, maxLength: Int = 128, maxLines: Int = 1, content: @Composable () -> Unit = {}) {

    Box(modifier = Modifier
        .clip(AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
        .then(
            if (warning) Modifier
                .clip(AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                .border(2.dp, Color(0xff027BFF), AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
            else Modifier
        )
        .background(
            Color(if (required) 0xfff3f3f3 else 0xfff1f1f1),
            shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60)
        )
        .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (label.isNotEmpty()) {
                Text(label, style = TextStyle(color = Color(0xff717171), fontWeight = FontWeight.Normal), modifier = Modifier.width(54.dp))
            }
            BasicTextField(
                value = string.value,
                textStyle = TextStyle(color = Color(0xff414141), fontWeight = FontWeight.Bold),
                maxLines = maxLines,
                decorationBox = {
                    if (string.value.isEmpty()) {
                        Text(placeholder, style = TextStyle(color = Color(0xffb1b1b1)))
                    }
                    it()
                },
                onValueChange = {
                    if (customRegex.isEmpty()) {
                        if (it.length <= maxLength) {
                            string.value = it
                        }
                    }
                    else {
                        if ((it.matches(Regex(customRegex)) || it.isEmpty()) && it.length <= maxLength) {
                            string.value = it
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            content()
        }
    }
}

@Composable
fun ParaTranzInputView(label: String = "", placeholder: String = "", customRegex: String = "", string: MutableState<String>, required: Boolean = false, warning: Boolean = false, maxLength: Int = 128, maxLines: Int = 1, showHint: MutableState<Boolean>, hint: String, content: @Composable () -> Unit = {}) {

    val showHintAnimation = remember { Animatable(0f) }

    LaunchedEffect(showHint.value) {
        showHintAnimation.animateTo(if (showHint.value) 1f else 0f, tween(400))
    }


    Box(modifier = Modifier
        .clip(AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
        .then(
            if (warning) Modifier
                .clip(AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                .border(2.dp, Color(0xff027BFF), AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
            else Modifier
        )
        .then(
            if (showHint.value) Modifier
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable {
                    showHint.value = false
                }
            else Modifier
        )
        .background(
            Color(if (required) 0xfff3f3f3 else 0xfff1f1f1),
            shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60)
        )
        .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier
            .alpha(1 - showHintAnimation.value)
            .scale(1 - ((showHintAnimation.value) * 0.2f))
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (label.isNotEmpty()) {
                Text(label, style = TextStyle(color = Color(0xff717171), fontWeight = FontWeight.Normal), modifier = Modifier.width(54.dp))
            }
            BasicTextField(
                value = string.value,
                textStyle = TextStyle(color = Color(0xff414141), fontWeight = FontWeight.Bold),
                maxLines = maxLines,
                enabled = !showHint.value,
                decorationBox = {
                        if (string.value.isEmpty()) {
                            Text(placeholder, style = TextStyle(color = Color(0xffb1b1b1)))
                        }
                        it()
                },
                onValueChange = {
                    if (customRegex.isEmpty()) {
                        if (it.length <= maxLength) {
                            string.value = it
                        }
                    }
                    else {
                        if ((it.matches(Regex(customRegex)) || it.isEmpty()) && it.length <= maxLength) {
                            string.value = it
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            )
            content()
        }
        if (hint.isNotEmpty() && (showHint.value || showHintAnimation.value != 0f)){
            Box(modifier = Modifier
                .clip(AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                .fillMaxWidth()
                .alpha(showHintAnimation.value)
                .scale(1 + ((1 - showHintAnimation.value) * 0.2f)),
                contentAlignment = Alignment.Center) {
                Text(hint, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.pointerHoverIcon(
                    PointerIcon.Hand), textAlign = TextAlign.Center, style = TextStyle(color = Color(0xff027BFF), fontWeight = FontWeight.SemiBold))
            }
        }
    }
}