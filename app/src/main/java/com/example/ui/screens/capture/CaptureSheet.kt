package com.example.ui.screens.capture

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.LifeOSApp
import com.example.domain.model.CaptureType
import com.example.domain.model.Mood
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentPink
import com.example.ui.theme.PrimaryIndigo
import com.example.util.AudioRecorderHelper
import com.example.util.MediaStorageHelper
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureSheet(
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var selectedType by remember { mutableStateOf(CaptureType.THOUGHT) }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<Mood?>(Mood.HAPPY) }
    var tags by remember { mutableStateOf("#daily") }

    // Media states
    var capturedMediaPath by remember { mutableStateOf<String?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraPath by remember { mutableStateOf<String?>(null) }

    // Audio recording state
    val audioRecorder = remember { AudioRecorderHelper(context) }
    var isRecordingAudio by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableIntStateOf(0) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            audioRecorder.release()
        }
    }

    // Camera photo launcher
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraPath != null) {
            capturedMediaPath = pendingCameraPath
        }
    }

    // Gallery photo picker launcher
    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = MediaStorageHelper.copyUriToInternalStorage(
                context = context,
                sourceUri = uri,
                subFolder = "photos",
                prefix = "photo_gallery",
                extension = "jpg"
            )
            capturedMediaPath = savedPath
        }
    }

    // Camera video launcher
    val captureVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && pendingCameraPath != null) {
            capturedMediaPath = pendingCameraPath
        }
    }

    // Gallery video picker launcher
    val pickVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = MediaStorageHelper.copyUriToInternalStorage(
                context = context,
                sourceUri = uri,
                subFolder = "videos",
                prefix = "video_gallery",
                extension = "mp4"
            )
            capturedMediaPath = savedPath
        }
    }

    // Audio permission launcher
    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val path = audioRecorder.startRecording { seconds ->
                recordingDuration = seconds
            }
            if (path != null) {
                isRecordingAudio = true
                capturedMediaPath = path
            }
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (selectedType == CaptureType.PHOTO) {
                val (uri, path) = MediaStorageHelper.createImageFileUri(context)
                pendingCameraUri = uri
                pendingCameraPath = path
                takePhotoLauncher.launch(uri)
            } else if (selectedType == CaptureType.VIDEO) {
                val (uri, path) = MediaStorageHelper.createVideoFileUri(context)
                pendingCameraUri = uri
                pendingCameraPath = path
                captureVideoLauncher.launch(uri)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Capture ⚡",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Instant & Private",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Capture Type Selector (Thought, Photo, Audio, Video)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val types = listOf(
                    Triple(CaptureType.THOUGHT, Icons.Default.Lightbulb, "Thought"),
                    Triple(CaptureType.PHOTO, Icons.Default.CameraAlt, "Photo"),
                    Triple(CaptureType.AUDIO, Icons.Default.Audiotrack, "Audio"),
                    Triple(CaptureType.VIDEO, Icons.Default.Videocam, "Video")
                )

                types.forEach { (type, icon, label) ->
                    val isSelected = selectedType == type
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) PrimaryIndigo.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) PrimaryIndigo else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedType = type
                                capturedMediaPath = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("capture_type_${label.lowercase()}")
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Media Action Area based on Type
            when (selectedType) {
                CaptureType.PHOTO -> {
                    if (capturedMediaPath != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = File(capturedMediaPath!!),
                                contentDescription = "Captured Photo",
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = {
                                    MediaStorageHelper.deleteFile(capturedMediaPath!!)
                                    capturedMediaPath = null
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasPermission) {
                                        val (uri, path) = MediaStorageHelper.createImageFileUri(context)
                                        pendingCameraUri = uri
                                        pendingCameraPath = path
                                        takePhotoLauncher.launch(uri)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("capture_camera_button")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Take Photo", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    pickPhotoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("capture_gallery_photo_button")
                            ) {
                                Text("Choose Photo", fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                CaptureType.VIDEO -> {
                    if (capturedMediaPath != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Videocam, contentDescription = "Video", tint = PrimaryIndigo)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Video clip recorded 🎥", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Saved in app memory", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = {
                                    MediaStorageHelper.deleteFile(capturedMediaPath!!)
                                    capturedMediaPath = null
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentPink)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasPermission) {
                                        val (uri, path) = MediaStorageHelper.createVideoFileUri(context)
                                        pendingCameraUri = uri
                                        pendingCameraPath = path
                                        captureVideoLauncher.launch(uri)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("capture_record_video_button")
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = "Video", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Record Video", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    pickVideoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("capture_gallery_video_button")
                            ) {
                                Text("Choose Video", fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                CaptureType.AUDIO -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isRecordingAudio) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(AccentPink)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Recording: ${String.format("%02d:%02d", recordingDuration / 60, recordingDuration % 60)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = AccentPink
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        val path = audioRecorder.stopRecording()
                                        isRecordingAudio = false
                                        capturedMediaPath = path
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Stop Recording")
                                }
                            } else if (capturedMediaPath != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                if (isPlayingAudio) {
                                                    audioRecorder.stopPlayback()
                                                    isPlayingAudio = false
                                                } else {
                                                    audioRecorder.playAudio(capturedMediaPath!!) {
                                                        isPlayingAudio = false
                                                    }
                                                    isPlayingAudio = true
                                                }
                                            },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryIndigo)
                                        ) {
                                            Icon(
                                                imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = if (isPlayingAudio) "Stop" else "Play",
                                                tint = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(if (isPlayingAudio) "Playing voice note 🔊" else "Audio Memo Ready 🎙️", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("${recordingDuration}s duration", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    IconButton(onClick = {
                                        audioRecorder.stopPlayback()
                                        MediaStorageHelper.deleteFile(capturedMediaPath!!)
                                        capturedMediaPath = null
                                        recordingDuration = 0
                                        isPlayingAudio = false
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentPink)
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        val hasPermission = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.RECORD_AUDIO
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (hasPermission) {
                                            val path = audioRecorder.startRecording { seconds ->
                                                recordingDuration = seconds
                                            }
                                            if (path != null) {
                                                isRecordingAudio = true
                                                capturedMediaPath = path
                                            }
                                        } else {
                                            recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("start_audio_recording_button")
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = "Mic")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tap to Record Voice Note")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                CaptureType.THOUGHT -> {
                    // Handled by title and note inputs below
                }
            }

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        when (selectedType) {
                            CaptureType.PHOTO -> "Photo title (e.g. Morning Coffee, Sunrise)..."
                            CaptureType.AUDIO -> "Audio memo topic..."
                            CaptureType.VIDEO -> "Video clip title..."
                            CaptureType.THOUGHT -> "What's on your mind right now?..."
                        }
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capture_title_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Note / Caption Field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Add extra context, details, or feelings (optional)...") },
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capture_note_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mood Selector
            Text(
                text = "Current Mood:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Mood.values()) { mood ->
                    val isSelected = selectedMood == mood
                    Surface(
                        color = if (isSelected) PrimaryIndigo.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { selectedMood = mood }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(mood.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = mood.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tags
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                placeholder = { Text("Tags (e.g. #coffee, #gym, #study)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capture_tags_input")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Save Button
            Button(
                onClick = {
                    scope.launch {
                        val mediaPath = capturedMediaPath ?: ""
                        LifeOSApp.repo.saveCapture(
                            type = selectedType,
                            title = title.ifBlank {
                                when (selectedType) {
                                    CaptureType.PHOTO -> "Photo memory"
                                    CaptureType.AUDIO -> "Voice note (${recordingDuration}s)"
                                    CaptureType.VIDEO -> "Video clip"
                                    CaptureType.THOUGHT -> "Quick thought"
                                }
                            },
                            note = note,
                            mediaUri = mediaPath,
                            mood = selectedMood,
                            tags = tags
                        )
                        onSaved()
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("capture_save_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save to Life Timeline", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
