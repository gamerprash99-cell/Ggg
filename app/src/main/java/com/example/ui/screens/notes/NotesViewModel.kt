package com.example.ui.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.LifeOSApp
import com.example.ai.LifeOSAI
import com.example.data.local.entity.NoteEntity
import com.example.data.repository.LifeOSRepository
import com.example.domain.model.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotesUiState(
    val notes: List<NoteEntity> = emptyList(),
    val folders: List<String> = listOf("All", "Ideas", "College", "Projects", "Personal", "Finance"),
    val selectedFolder: String = "All",
    val searchQuery: String = "",
    val activeAIDialogNote: NoteEntity? = null,
    val aiSummaryResult: String? = null,
    val aiExtractedTasks: List<String> = emptyList(),
    val isAILoading: Boolean = false
)

class NotesViewModel(
    private val repository: LifeOSRepository = LifeOSApp.repo
) : ViewModel() {

    private val _selectedFolder = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")
    private val _activeAIDialogNote = MutableStateFlow<NoteEntity?>(null)
    private val _aiSummaryResult = MutableStateFlow<String?>(null)
    private val _aiExtractedTasks = MutableStateFlow<List<String>>(emptyList())
    private val _isAILoading = MutableStateFlow(false)

    private val filteredNotes = combine(
        repository.getAllNotes(),
        _selectedFolder,
        _searchQuery
    ) { notes, folder, query ->
        val filtered = notes.filter { note ->
            val matchesFolder = folder == "All" || note.folder.equals(folder, ignoreCase = true)
            val matchesQuery = query.isBlank() || note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true) ||
                    note.tags.contains(query, ignoreCase = true)
            matchesFolder && matchesQuery
        }
        Triple(filtered, folder, query)
    }

    val uiState: StateFlow<NotesUiState> = combine(
        filteredNotes,
        _activeAIDialogNote,
        _aiSummaryResult,
        _aiExtractedTasks,
        _isAILoading
    ) { (notes, folder, query), aiNote, summary, tasks, loading ->
        NotesUiState(
            notes = notes,
            selectedFolder = folder,
            searchQuery = query,
            activeAIDialogNote = aiNote,
            aiSummaryResult = summary,
            aiExtractedTasks = tasks,
            isAILoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesUiState()
    )

    fun selectFolder(folder: String) {
        _selectedFolder.value = folder
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.togglePinNote(note)
        }
    }

    fun toggleFavorite(note: NoteEntity) {
        viewModelScope.launch {
            repository.toggleFavoriteNote(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    suspend fun getNoteById(id: Long): NoteEntity? {
        return repository.getNoteById(id)
    }

    fun saveNote(title: String, content: String, folder: String, tags: String, isPinned: Boolean, noteId: Long) {
        viewModelScope.launch {
            repository.saveNote(
                title = title.ifBlank { "Untitled Note" },
                content = content,
                folder = folder,
                tags = tags,
                isPinned = isPinned,
                existingId = noteId
            )
        }
    }

    // --- AI SMART NOTES ---
    fun openAIDialog(note: NoteEntity) {
        _activeAIDialogNote.value = note
        _aiSummaryResult.value = null
        _aiExtractedTasks.value = emptyList()
    }

    fun closeAIDialog() {
        _activeAIDialogNote.value = null
        _aiSummaryResult.value = null
        _aiExtractedTasks.value = emptyList()
    }

    fun requestAISummary(note: NoteEntity) {
        _isAILoading.value = true
        viewModelScope.launch {
            val summary = LifeOSAI.summarizeNote(note.title, note.content)
            _aiSummaryResult.value = summary
            _isAILoading.value = false
        }
    }

    fun requestAITaskExtraction(note: NoteEntity) {
        _isAILoading.value = true
        viewModelScope.launch {
            val tasks = LifeOSAI.extractTasksFromText("${note.title}\n${note.content}")
            _aiExtractedTasks.value = tasks
            _isAILoading.value = false
        }
    }

    fun approveAndCreateExtractedTasks(tasks: List<String>) {
        viewModelScope.launch {
            tasks.forEach { taskTitle ->
                repository.addTask(
                    title = taskTitle,
                    priority = Priority.MEDIUM,
                    category = "Notes"
                )
            }
            closeAIDialog()
        }
    }
}
