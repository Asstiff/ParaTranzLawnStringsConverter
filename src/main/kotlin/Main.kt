import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import views.*
import views.ParaTranzButtonTypes.*
import java.awt.Cursor

@Composable
@Preview
fun App() {
    val message = remember {mutableStateOf("")}
    val showSettings = remember {mutableStateOf(false)}

    val settingsAlpha = remember { Animatable(0f) }
    val settingsWidth = remember { Animatable(140f) }
    val settingsHeight = remember { Animatable(48f) }
    val settingsPadding = remember { Animatable(12f) }
    val settingsColor = remember { Animatable(Color(0xff027BFF)) }
    val settingsScaleAnimation = remember { Animatable(1f) }

    val scaleAnimation = remember { Animatable( 0.8f ) }
    val alphaAnimation = remember { Animatable( 0f ) }

    var blurTarget by remember { mutableStateOf(18.dp) }
    val blurAnimation = animateDpAsState(blurTarget, tween(200, easing = CubicBezierEasing(0.1f, 0.2f, 0.4f, 1.0f)))

    LaunchedEffect(showSettings.value){
        launch {
            settingsWidth.animateTo(if(showSettings.value) 340f else 140f, spring(1.0f, 400f) )
        }
        launch {
            settingsHeight.animateTo(if(showSettings.value) 400f else 48f, spring(0.9f, 500f))
        }
        launch {
            settingsPadding.animateTo(if(showSettings.value) 30f else 12f, spring(0.7f, 200f))
        }
        launch {
            settingsAlpha.animateTo(if(showSettings.value) 1f else 0f, spring(1.0f, 100f))
        }
        launch {
            settingsScaleAnimation.animateTo(if(showSettings.value) 0.9f else 1f, tween(500, easing = CubicBezierEasing(0.5f, 1.3f, 0.3f, 0.95f)))
        }
        launch {
            settingsColor.animateTo(if(showSettings.value) Color.White else Color(0xff027BFF), tween(300, easing = CubicBezierEasing(0.5f, 1f, 0.3f, 0.95f)))
        }
    }

    LaunchedEffect(message.value.isNotEmpty()){
        launch {
            if (message.value.isEmpty()){
                scaleAnimation.snapTo(0.8f)
            }
            else{
                scaleAnimation.animateTo(1f, tween(350, easing = CubicBezierEasing(0f, 1f, 0.4f, 1.0f)))
            }
        }
        launch {
            if (message.value.isEmpty()){
                alphaAnimation.snapTo(0f)
            }
            else{
                alphaAnimation.animateTo(1f, tween(350, easing = CubicBezierEasing(0f, 1f, 0.4f, 1.0f)))
            }
        }
        launch {
            if (message.value.isEmpty()){
                blurTarget = 18.dp
            }
            else{
                blurTarget = 0.dp
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.background(Color.White).scale(settingsScaleAnimation.value)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(20.dp)) {
                Image(painterResource("images/paratranz_logo.png"), contentDescription = "", modifier = Modifier.height(height = 38.dp))
                Text("Converter", style = TextStyle(fontSize = 18.sp, color = Color(0xff707070)), fontWeight = FontWeight.Bold)
                Text("for LawnStrings", style = TextStyle(fontSize = 12.sp, color = Color(0xff707070)), fontWeight = FontWeight.Normal)
            }
            MainView(message)
        }
            Box(
                Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
            if (showSettings.value || settingsAlpha.value != 0f){
                Box(modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = showSettings.value,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            showSettings.value = false
                        }
                    )
                    .alpha(settingsAlpha.value)
                    .background(Color(0x36000000))
                )
            }
                Box(
                    modifier = Modifier
                        .padding(settingsPadding.value.dp)
                        .then(
                            if (!showSettings.value) Modifier.pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                            else Modifier
                        )
                        .shadow(16.dp, shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                        .clip(shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                        .clickable(enabled = !showSettings.value) {
                            showSettings.value = true
                        }
                        .background(settingsColor.value, shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                        .width(settingsWidth.value.dp)
                        .height(settingsHeight.value.dp)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    ParaTranzSettingsView()
                    Text("ParaTranz API", style = TextStyle(color = Color(0xfff3f3f3), fontSize = 14.sp), textAlign = TextAlign.Center, maxLines = 1, modifier = Modifier.alpha((1 - settingsAlpha.value)))
                }
            }

        if (message.value.isNotEmpty()) {
            Box(
                Modifier
                    .alpha(alphaAnimation.value)
                    .blur(blurAnimation.value)
                    .background(Color(0x36000000))
                    .fillMaxSize()
                    .clickable(
                        enabled = true,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .scale(scaleAnimation.value)
                        .shadow(18.dp - blurAnimation.value, shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                        .background(Color.White, shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                        .width(340.dp)
                        .padding(12.dp)
                        .heightIn(max = 220.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("提示", style = TextStyle(fontSize = 16.sp, color = Color(0xff707070)), fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), color = Color(0x20707070))
                    Column(modifier = Modifier.padding(vertical = 24.dp), verticalArrangement = Arrangement.Center){
                        Text(message.value, style = TextStyle(fontSize = 14.sp, color = Color(0xff484848)))
                    }
                    ParaTranzButton(type = ParaTranzButtonTypes.SUGGESTED, lable = "好", onclick = {
                        message.value = ""
                    })
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
