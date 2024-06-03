import APIConfig.showSettings
import ImportedFile.setFile
import Window.windowPinned
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.*
import views.AbsoluteSmoothCornerShape
import views.MainView
import views.ParaTranzButton
import views.ParaTranzButtonTypes.SUGGESTED
import views.ParaTranzSettingsView
import java.awt.Cursor
import java.awt.Dimension
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.lang.Thread.sleep
import javax.swing.JFrame
import kotlin.math.max

enum class ImportedFileType{
    PARA, LAWN, NONE
}

object APIConfig {
    val apiKey = mutableStateOf("")
    val projectId = mutableStateOf("")
    val fileName = mutableStateOf("")
    val showSettings = mutableStateOf(false)
}

object Window {
    val windowPinned = mutableStateOf(false)
}

object Message {
    private val message = mutableStateOf("")
    private val showMessage = mutableStateOf(false)

    fun showMessage(message: String) {
        this.message.value = message
        showMessage.value = true
    }

    fun clearMessage() {
        CoroutineScope(Dispatchers.IO).launch {
            showMessage.value = false
            sleep(350)
            message.value = ""
        }
    }

    fun isMessageEmpty() = this.message.value.isEmpty()
    fun getMessage() = this.message.value
    fun getShowMessage() = this.showMessage.value
}

object ImportedFile {
    var fileContent: MutableState<String> = mutableStateOf("")
    var loadedFilename: MutableState<String> = mutableStateOf("")
    var fileType: MutableState<ImportedFileType> = mutableStateOf(ImportedFileType.NONE)
    var loading = mutableStateOf(false)
    var isDroppable =  mutableStateOf(false)
    private var job: Job? = null

    fun setFile(file: File) {

        if (file.length() < 10000000) {
            loading.value = true

            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    loadedFilename.value = file.nameWithoutExtension
                    val content = withContext(Dispatchers.IO) {
                        file.readTextWithCancellation()
                    }
                    fileContent.value = content

                    if (fileContent.value.isNotEmpty() && fileContent.value.contains("\"LawnStringsData\"")) {
                        fileType.value = ImportedFileType.LAWN
                    } else if (fileContent.value.isNotEmpty() && fileContent.value.contains("\"key\"") && fileContent.value.contains("\"original\"") && fileContent.value.contains("\"translation\"")) {
                        fileType.value = ImportedFileType.PARA
                    } else {
                        fileType.value = ImportedFileType.NONE
                        Message.showMessage("无法确定载入文件格式，但你依然可以尝试进行转换。")
                    }
                } catch (e: Throwable) {
                    withContext(Dispatchers.IO) {
                        fileType.value = ImportedFileType.NONE
                        fileContent.value = ""
                        loadedFilename.value = ""
                        Message.showMessage("文件读取失败：\n${e.message}\nWhatever that means.")
                    }
                } finally {
                    loading.value = false
                }
            }
        }
        else{
            Message.showMessage("文件过大。")
        }
    }

    fun cancelJob() {
        job?.cancel()
        job = null
        loading.value = false
        fileContent.value = ""
        loadedFilename.value = ""
        fileType.value = ImportedFileType.NONE
    }
}

suspend fun File.readTextWithCancellation(): String = withContext(Dispatchers.IO) {
    val builder = StringBuilder()
    this@readTextWithCancellation.forEachLine { line ->
        ensureActive()  // 检查协程是否仍然活跃，如果已经取消则抛出异常
        builder.append(line).append("\n")
    }
    builder.toString()
}


@Composable
@Preview
fun App(window: JFrame) {
    val settingsAlpha = remember { Animatable(0f) }
    val settingsWidth = remember { Animatable(120f) }
    val settingsHeight = remember { Animatable(42f) }
    val settingsPadding = remember { Animatable(12f) }
    val settingsColor = remember { Animatable(Color(0xff027BFF)) }
    val settingsScaleAnimation = remember { Animatable(1f) }
    val settingsContentScaleAnimation = remember { Animatable(0f) }

    val scaleAnimation = remember { Animatable( 0.8f ) }
    val alphaAnimation = remember { Animatable( 0f ) }


    LaunchedEffect(showSettings.value){
        launch {
            settingsWidth.animateTo(if(showSettings.value) 392f else 120f, spring(1.0f, 400f) )
        }
        launch {
            settingsHeight.animateTo(if(showSettings.value) 300f else 42f, spring(0.9f, 500f))
        }
        launch {
            settingsPadding.animateTo(if(showSettings.value) 30f else 12f, spring(0.7f, 200f))
        }
        launch {
            settingsAlpha.animateTo(if(showSettings.value) 1f else 0f, spring(1.0f, 100f))
        }
        launch {
            settingsScaleAnimation.animateTo(if(showSettings.value) 0.95f else 1f, tween(500, easing = CubicBezierEasing(0.5f, 1.3f, 0.3f, 0.95f)))
        }
        launch {
            settingsContentScaleAnimation.animateTo(if(showSettings.value) 1f else 0f, tween(270, delayMillis = if (showSettings.value) 100 else 0, easing = CubicBezierEasing(0.2f, 0.65f, 0f, 1f)))
        }
        launch {
            settingsColor.animateTo(if(showSettings.value) Color.White else Color(0xff027BFF), tween(300, easing = CubicBezierEasing(0.5f, 1f, 0.3f, 0.95f)))
        }
    }

    LaunchedEffect(Message.getShowMessage()){
        launch {
            if (!Message.getShowMessage()){
                scaleAnimation.animateTo(0.8f, tween(350, easing = CubicBezierEasing(0.0f, 0.75f, 0f, 1f)))
            }
            else{
                scaleAnimation.animateTo(1f, tween(350, easing = CubicBezierEasing(0f, 1f, 0.4f, 1.0f)))
            }
        }
        launch {
            if (!Message.getShowMessage()){
                alphaAnimation.animateTo(0f, tween(350, easing = CubicBezierEasing(0.0f, 0.75f, 0f, 1f)))
            }
            else{
                alphaAnimation.animateTo(1f, tween(350, easing = CubicBezierEasing(0f, 1f, 0.4f, 1.0f)))
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .background(Color.White)
            .scale(settingsScaleAnimation.value)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 36.dp, bottom = 24.dp)) {
                Image(painterResource("images/paratranz_logo.png"), contentDescription = "", modifier = Modifier.height(height = 38.dp))
                Text("Converter", style = TextStyle(fontSize = 18.sp, color = Color(0xff707070)), fontWeight = FontWeight.Bold)
                Text("for LawnStrings", style = TextStyle(fontSize = 12.sp, color = Color(0xff707070)), fontWeight = FontWeight.Normal)
            }
            MainView()
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
                    .shadow(((settingsAlpha.value)*16f).dp, shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
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
                ParaTranzSettingsView(modifier = Modifier
                    .graphicsLayer(
                        scaleX = max(settingsContentScaleAnimation.value, 0.6f),
                        scaleY = max(settingsContentScaleAnimation.value, 0.6f),
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    )
                    .alpha(settingsContentScaleAnimation.value)
                )
                Text("ParaTranz API", style = TextStyle(color = Color(0xfff3f3f3), fontSize = 14.sp), textAlign = TextAlign.Center, maxLines = 1, modifier = Modifier.alpha((1 - settingsAlpha.value)))
            }
        }

        if (!Message.isMessageEmpty() || alphaAnimation.value != 0f) {
            Box(
                Modifier
                    .alpha(alphaAnimation.value)
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
                        .shadow(18.dp, shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                        .background(Color.White, shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60))
                        .width(340.dp)
                        .padding(12.dp)
                        .heightIn(min = 140.dp,max = 320.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("提示", style = TextStyle(fontSize = 16.sp, color = Color(0xff707070)), fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), color = Color(0x20707070))
                    Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 12.dp), verticalArrangement = Arrangement.Center){
                        Text(Message.getMessage(), style = TextStyle(fontSize = 14.sp, color = Color(0xff484848)), textAlign = TextAlign.Center, maxLines = 4, overflow = TextOverflow.Ellipsis)
                    }
                    ParaTranzButton(type = SUGGESTED, lable = "好", onclick = {
                        Message.clearMessage()
                    })
                }
            }
        }
    }

    LaunchedEffect(window) {
        window.dropTarget = object : DropTarget() {
            @Suppress("UNCHECKED_CAST")
            override fun drop(evt: DropTargetDropEvent) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY)
                    val droppedFiles = evt.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                    if (droppedFiles.isNotEmpty()) {
                        val file = droppedFiles[0]
                        setFile(file)
                        ImportedFile.isDroppable.value = false
                    }
                } catch (ex: Exception) {
                    Message.showMessage("确保拖入的是一个文件。")
                }
            }

            override fun dragEnter(evt: java.awt.dnd.DropTargetDragEvent) {
                ImportedFile.isDroppable.value = true
            }

            override fun dragExit(evt: java.awt.dnd.DropTargetEvent) {
                ImportedFile.isDroppable.value = false
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, state = WindowState(width = 520.dp, height = 422.dp), icon = painterResource("images/icon_512x512@2x.png")) {
        window.minimumSize = Dimension(520, 422)
        window.isResizable = false
        window.isAlwaysOnTop = windowPinned.value
        window.name = "ParaTranz Converter"
        window.title = "ParaTranz Converter for LawnStrings"
        App(window)
    }
}
