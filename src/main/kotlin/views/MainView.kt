package views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import utils.ParaTranzConverter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.*
import java.awt.Cursor
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

enum class FileType{
    PARA, LAWN, NONE
}

@Composable
@Preview
fun MainView(message: MutableState<String>){
    var fileContent by remember { mutableStateOf<String?>(null) }
    var fileType: MutableState<FileType> = remember { mutableStateOf(FileType.NONE) }
    var versionText by remember { mutableStateOf("") }
    var isRelChecked by remember { mutableStateOf(false) }
    var buildNumber by remember { mutableStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    var loadedFilename by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    Column(
        modifier = Modifier.padding(12.dp).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column( verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier
                .background(
                    Color(0xfff1f1f1),
                    shape = AbsoluteSmoothCornerShape(cornerRadius = 16.dp, smoothnessAsPercent = 60)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("版本号", style = TextStyle(color = Color(0xff717171), fontWeight = FontWeight.Normal))
                BasicTextField(
                    value = versionText,
                    textStyle = TextStyle(color = Color(0xff414141), fontWeight = FontWeight.Bold),
                    onValueChange = { versionText = it },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    decorationBox = { innerTextField ->
                        if (versionText.isEmpty()) {
                            Text("11.4.1", style = TextStyle(color = Color(0xffb1b1b1)))
                        }
                        innerTextField()
                    }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(modifier = Modifier
                    .weight(1f)
                    .background(
                        Color(0xfff1f1f1),
                        shape = AbsoluteSmoothCornerShape(
                            cornerRadiusTL = 16.dp, cornerRadiusBL = 16.dp,
                            cornerRadiusBR = 4.dp, cornerRadiusTR = 4.dp,
                            smoothnessAsPercentTL = 60, smoothnessAsPercentBL = 60,
                            smoothnessAsPercentBR = 60, smoothnessAsPercentTR = 60,
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("构建号", style = TextStyle(color = Color(0xff717171), fontWeight = FontWeight.Normal))
                    BasicTextField(
                        value = buildNumber.toString(),
                        textStyle = TextStyle(color = Color(0xff414141), fontWeight = FontWeight.Bold),
                        onValueChange = {
                            if (it.matches(Regex("^\\d+\$")) || it.isEmpty()){
                                if (it.isEmpty()){
                                    buildNumber = 0
                                }
                                else {
                                    try {
                                        if (buildNumber == 0 && it.last() == '0')
                                            buildNumber = it.substring(0, it.lastIndex).toInt()
                                        else {
                                            buildNumber = it.toInt()
                                        }
                                    }
                                    catch (_: NumberFormatException){
                                        buildNumber = 0
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        decorationBox = { innerTextField ->
                            if (buildNumber.toString().isEmpty()) {
                                Text("Number")
                            }
                            innerTextField()
                        }
                    )
                }
                Row(modifier = Modifier
                    .background(
                        Color(0xfff1f1f1),
                        shape = AbsoluteSmoothCornerShape(
                            cornerRadiusTL = 4.dp, cornerRadiusBL = 4.dp,
                            cornerRadiusBR = 16.dp, cornerRadiusTR = 16.dp,
                            smoothnessAsPercentTL = 60, smoothnessAsPercentBL = 60,
                            smoothnessAsPercentBR = 60, smoothnessAsPercentTR = 60,
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("正式版", style = TextStyle(color = Color(0xff717171), fontWeight = FontWeight.Normal))
                    Box(modifier = Modifier
                        .padding(vertical = 6.dp)
                        .padding(start = 8.dp)
                        .clip(AbsoluteSmoothCornerShape(cornerRadius = 6.dp, smoothnessAsPercent = 60))
                        .size(18.dp)
                        .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                        .clickable { isRelChecked = !isRelChecked }
                        .then(
                            if (isRelChecked)
                                Modifier
                                    .background(Color(0xff027BFF))
                                    .border(width = 2.dp, color = Color(0x88027BFF), shape = AbsoluteSmoothCornerShape(cornerRadius = 6.dp, smoothnessAsPercent = 60))
                            else
                                Modifier
                                    .background(Color(0xffe1e1e1))
                        ),
                        contentAlignment = Alignment.Center
                    ){
                        if (isRelChecked){
                            Image(painter = painterResource("images/done_20dp.svg"), contentDescription = "checkmark",
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ParaTranzButton(ParaTranzButtonTypes.CLEAR,
                imagePath = if (loadedFilename.isNotEmpty() || !fileContent.isNullOrEmpty()) "images/repick.svg" else "images/import.svg",
                lable = if (loadedFilename.isNotEmpty() || !fileContent.isNullOrEmpty()) loadedFilename else "上传文件",
                onclick = {
                    if (loading){
                        if (job != null){
                            job!!.cancel()
                            loading = false
                            fileContent = ""
                            loadedFilename = ""
                        }
                    }
                    else{
                        val file = chooseFile("选择文件")
                        if (file != null) {
                            if (file.length() < 10000000){
                                loading = true
                                job = scope.launch(Dispatchers.IO) {
                                    try {
                                        loadedFilename = file.nameWithoutExtension
                                        fileContent = file.readText()

                                        if (!fileContent.isNullOrEmpty() && fileContent!!.contains("\"LawnStringsData\"")){
                                            fileType.value = FileType.LAWN
                                        }
                                        else if (!fileContent.isNullOrEmpty() && fileContent!!.contains("[\n" +
                                                    "  {\n" +
                                                    "    \"key\"")){
                                            fileType.value = FileType.PARA
                                        }
                                        else {
                                            fileType.value = FileType.NONE
                                            message.value = "无法确定输入文件格式。"
                                        }
                                        withContext(Dispatchers.Default) {
                                            loading = false
                                        }
                                    }
                                    catch (_: Exception){
                                        loading = false
                                        fileContent = ""
                                        loadedFilename = ""
                                    }
                                }
                            }
                            else{
                                message.value = "文件过大。"
                            }
                        }
                    }
            }){
                if (loading) {
                    CircularProgressIndicator(strokeCap = StrokeCap.Round, color = Color(0xff027BFF), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ParaTranzButton(
                    type = if ((fileType.value == FileType.LAWN)) ParaTranzButtonTypes.SUGGESTED else ParaTranzButtonTypes.NORMAL,
                    lable = "转为 ParaTranz",
                    enabled = !fileContent.isNullOrEmpty() && (fileType.value != FileType.PARA),
                    onclick = {
                    if (fileContent != null) {
                        val file = saveFile("保存文件", "LawnStrings-ParaTranz.json")
                        if (file != null) {
                            scope.launch(Dispatchers.IO) {
                                try {
                                    ParaTranzConverter.toParaTranz(fileContent!!, file)
                                    withContext(Dispatchers.Default) {
                                        message.value = "文件已成功转换为 ParaTranz 格式"
                                    }
                                }
                                catch (_ : Exception){
                                    message.value = "导出出错。"
                                }
                            }
                        }
                    } else {
                        message.value = "请先上传文件"
                    }
                })

                ParaTranzButton(
                    type = if ((fileType.value == FileType.PARA)) ParaTranzButtonTypes.SUGGESTED else ParaTranzButtonTypes.NORMAL,
                    enabled = !fileContent.isNullOrEmpty() && (fileType.value != FileType.LAWN),
                    lable = "转为 LawnStrings",
                    onclick = {
                    if (fileContent != null && versionText.isNotEmpty()) {
                        val versionMain = versionText.split('.').take(2).joinToString(".")
                        val versionFull = versionText
                        val relPre = if (isRelChecked) "REL" else "PRE"
                        val file = saveFile("保存文件", "LawnStrings-en-us.json")
                        if (file != null) {
                            scope.launch(Dispatchers.IO) {
                                try {
                                    ParaTranzConverter.toJson(fileContent!!, file, versionMain, versionFull, relPre, buildNumber)
                                    buildNumber++
                                    withContext(Dispatchers.Default) {
                                        message.value = "文件已成功转换为 LawnStrings 格式"
                                    }
                                }
                                catch (_ : Exception){
                                    message.value = "导出出错。"
                                }
                            }
                        }
                    } else {
                        message.value = "请先上传文件并输入版本信息"
                    }
                })
            }
        }
    }
}


fun chooseFile(title: String): File? {
    val dialog = FileDialog(Frame(), title, FileDialog.LOAD)
    dialog.isVisible = true
    return if (dialog.file != null) File(dialog.directory, dialog.file) else null
}

fun saveFile(title: String, initialFileName: String): File? {
    val dialog = FileDialog(Frame(), title, FileDialog.SAVE)
    dialog.file = initialFileName
    dialog.isVisible = true
    return if (dialog.file != null) File(dialog.directory, dialog.file) else null
}