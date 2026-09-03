package com.example.ai

import com.example.BuildConfig
import com.example.data.local.entity.DiaryEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.repository.LifeOSRepository
import com.example.domain.model.AIPermissions
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AIResponse(
    val answer: String,
    val cards: List<AICard> = emptyList(),
    val isExternalAI: Boolean = false
)

data class AICard(
    val title: String,
    val value: String,
    val subtitle: String = "",
    val type: String = "INFO"
)

data class AIProcessedDiary(
    val suggestedTitle: String,
    val refinedContent: String,
    val suggestedMood: Mood,
    val suggestedTags: String
)

object LifeOSAI {

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Answers queries using real local database records, respecting AI Context Permissions.
     */
    suspend fun answerQuestion(
        question: String,
        permissions: AIPermissions,
        repository: LifeOSRepository
    ): AIResponse = withContext(Dispatchers.IO) {
        val q = question.lowercase().trim()

        // 1. Task queries
        if (permissions.accessTasks && (q.contains("task") || q.contains("pending") || q.contains("todo") || q.contains("unfinished"))) {
            val pending = repository.getPendingTasks().first()
            if (pending.isEmpty()) {
                return@withContext AIResponse(
                    answer = "You have no unfinished tasks! Everything is completed.",
                    cards = listOf(AICard("Tasks", "0 Pending", "All caught up", "SUCCESS"))
                )
            } else {
                val listStr = pending.take(5).joinToString("\n") { "• ${it.title} [${it.priority}]" }
                val remaining = if (pending.size > 5) "\n...and ${pending.size - 5} more." else ""
                return@withContext AIResponse(
                    answer = "You currently have ${pending.size} pending tasks:\n\n$listStr$remaining",
                    cards = listOf(AICard("Pending Tasks", "${pending.size}", "High priority: ${pending.count { it.priority.name == "HIGH" }}", "TASK"))
                )
            }
        }

        // 2. Spending / Expense queries
        if (permissions.accessExpenses && (q.contains("spend") || q.contains("expense") || q.contains("money") || q.contains("cost") || q.contains("budget") || q.contains("food"))) {
            val expenses = repository.getAllExpenses().first()
            val total = expenses.sumOf { it.amount }
            val byCategory = expenses.groupBy { it.category }
            val topCategory = byCategory.maxByOrNull { entry -> entry.value.sumOf { it.amount } }

            val foodTotal = expenses.filter { it.category == ExpenseCategory.FOOD || it.category == ExpenseCategory.CAFE }.sumOf { it.amount }

            if (q.contains("food") || q.contains("cafe")) {
                return@withContext AIResponse(
                    answer = "You have spent a total of ₹${String.format(Locale.getDefault(), "%.0f", foodTotal)} on Food & Cafe across ${expenses.count { it.category == ExpenseCategory.FOOD || it.category == ExpenseCategory.CAFE }} entries.",
                    cards = listOf(AICard("Food Spending", "₹${String.format(Locale.getDefault(), "%.0f", foodTotal)}", "Food & Cafe total", "EXPENSE"))
                )
            }

            val topText = if (topCategory != null) {
                val catSum = topCategory.value.sumOf { it.amount }
                val pct = if (total > 0) (catSum / total * 100).toInt() else 0
                "\nLargest category is ${topCategory.key.displayName}: ₹${String.format(Locale.getDefault(), "%.0f", catSum)} ($pct%)."
            } else ""

            return@withContext AIResponse(
                answer = "Your recorded spending totals ₹${String.format(Locale.getDefault(), "%.0f", total)} across ${expenses.size} entries.$topText",
                cards = listOf(
                    AICard("Total Spending", "₹${String.format(Locale.getDefault(), "%.0f", total)}", "${expenses.size} expenses logged", "EXPENSE"),
                    AICard("Top Category", topCategory?.key?.displayName ?: "None", "Largest expense area", "CATEGORY")
                )
            )
        }

        // 3. Habits queries
        if (permissions.accessHabits && (q.contains("habit") || q.contains("streak") || q.contains("struggl") || q.contains("workout") || q.contains("water") || q.contains("read"))) {
            val habits = repository.getAllHabits().first()
            if (habits.isEmpty()) {
                return@withContext AIResponse(answer = "You haven't added any habits yet. Start with 'Drink Water' or 'Workout'!")
            }
            val best = habits.maxByOrNull { it.currentStreak }
            val struggling = habits.filter { it.currentStreak == 0 }
            val strugglingText = if (struggling.isNotEmpty()) {
                "\nHabits needing attention: " + struggling.joinToString(", ") { it.title }
            } else "\nAll habits have an active streak!"

            return@withContext AIResponse(
                answer = "You are tracking ${habits.size} habits. Your best active streak is ${best?.title} with 🔥 ${best?.currentStreak} days.$strugglingText",
                cards = listOf(
                    AICard("Best Streak", "${best?.title ?: "None"}", "🔥 ${best?.currentStreak ?: 0} Days", "HABIT"),
                    AICard("Tracking", "${habits.size} Habits", "Consistency focus", "INFO")
                )
            )
        }

        // 4. Notes queries
        if (permissions.accessNotes && (q.contains("note") || q.contains("college") || q.contains("project") || q.contains("idea"))) {
            val notes = repository.getAllNotes().first()
            val collegeNotes = notes.filter { it.folder.equals("College", ignoreCase = true) || it.tags.contains("college", ignoreCase = true) }
            val count = if (q.contains("college")) collegeNotes.size else notes.size
            val items = (if (q.contains("college")) collegeNotes else notes).take(4)

            val notesList = items.joinToString("\n") { "• ${it.title} (${it.folder})" }
            return@withContext AIResponse(
                answer = "Found $count notes in your second brain:\n\n$notesList",
                cards = listOf(AICard("Notes", "$count Total", "Organized by folders", "NOTE"))
            )
        }

        // 5. Diary / Reflection queries
        if (permissions.accessDiary && (q.contains("diary") || q.contains("journal") || q.contains("mood") || q.contains("feel"))) {
            val diaries = repository.getAllDiaries().first()
            if (diaries.isEmpty()) {
                return@withContext AIResponse(answer = "You haven't written any diary entries yet. Tap + Capture or open Life Hub -> Diary to write your first reflection!")
            }
            val moods = diaries.groupBy { it.mood }.maxByOrNull { it.value.size }
            val recent = diaries.take(3).joinToString("\n\n") { "📔 ${it.date} (${it.mood.emoji} ${it.mood.label}): ${it.title}\n\"${it.content.take(100)}...\"" }
            return@withContext AIResponse(
                answer = "You have written ${diaries.size} diary entries. Your dominant recorded mood is ${moods?.key?.emoji} ${moods?.key?.label}.\n\nRecent entries:\n$recent",
                cards = listOf(AICard("Diary Entries", "${diaries.size}", "Most frequent mood: ${moods?.key?.label}", "DIARY"))
            )
        }

        // 6. Life Summary / Week / Month / "How was my day / week / august"
        if (q.contains("summary") || q.contains("week") || q.contains("month") || q.contains("august") || q.contains("today") || q.contains("how was") || q.contains("life")) {
            return@withContext generateLifeSummary(repository, permissions)
        }

        // Optional external Gemini call if user configured a key and asks general questions
        val geminiKey = BuildConfig.GEMINI_API_KEY
        if (geminiKey.isNotEmpty() && !geminiKey.contains("MY_GEMINI_API_KEY")) {
            val externalAnswer = queryGeminiModel(question, geminiKey)
            if (externalAnswer != null) {
                return@withContext AIResponse(answer = externalAnswer, isExternalAI = true)
            }
        }

        // Fallback local intelligence
        return@withContext AIResponse(
            answer = "LifeOS AI is ready! You can ask me:\n" +
                    "• 'How was my week?' or 'Summarize my life'\n" +
                    "• 'How much did I spend on food?'\n" +
                    "• 'Show my unfinished tasks'\n" +
                    "• 'What habits am I struggling with?'\n" +
                    "• 'What notes did I create?'",
            cards = listOf(
                AICard("Privacy First", "100% Local", "Processed on-device", "PRIVACY"),
                AICard("Central AI", "Connected", "Notes, Tasks, Habits, Expenses", "AI")
            )
        )
    }

    private suspend fun generateLifeSummary(repository: LifeOSRepository, permissions: AIPermissions): AIResponse {
        val tasks = if (permissions.accessTasks) repository.getAllTasks().first() else emptyList()
        val habits = if (permissions.accessHabits) repository.getAllHabits().first() else emptyList()
        val expenses = if (permissions.accessExpenses) repository.getAllExpenses().first() else emptyList()
        val diaries = if (permissions.accessDiary) repository.getAllDiaries().first() else emptyList()

        val completedTasks = tasks.count { it.isCompleted }
        val taskRate = if (tasks.isNotEmpty()) (completedTasks * 100 / tasks.size) else 100
        val totalSpent = expenses.sumOf { it.amount }
        val topHabit = habits.maxByOrNull { it.currentStreak }
        val topCategory = expenses.groupBy { it.category }.maxByOrNull { entry -> entry.value.sumOf { it.amount } }

        val summary = buildString {
            append("📊 **Your LifeOS Overview**:\n\n")
            append("• **Tasks**: You completed $completedTasks of ${tasks.size} planned tasks ($taskRate% completion rate).\n")
            if (topHabit != null) {
                append("• **Habits**: Strongest habit is ${topHabit.title} with a streak of 🔥 ${topHabit.currentStreak} days.\n")
            }
            append("• **Finances**: ₹${String.format(Locale.getDefault(), "%.0f", totalSpent)} recorded spending.")
            if (topCategory != null) {
                append(" Largest category: ${topCategory.key.displayName}.\n")
            } else {
                append("\n")
            }
            append("• **Memories & Reflections**: ${diaries.size} diary reflections saved locally.\n\n")
            append("Overall, your system is running smoothly with offline privacy intact!")
        }

        return AIResponse(
            answer = summary,
            cards = listOf(
                AICard("Productivity", "$taskRate%", "$completedTasks / ${tasks.size} Tasks", "TASK"),
                AICard("Best Habit", topHabit?.title ?: "Active", "🔥 ${topHabit?.currentStreak ?: 0} Days", "HABIT"),
                AICard("Total Spent", "₹${String.format(Locale.getDefault(), "%.0f", totalSpent)}", "${expenses.size} expenses", "EXPENSE")
            )
        )
    }

    /**
     * Extracts actionable tasks from a note or raw thought text.
     */
    fun extractTasksFromText(text: String): List<String> {
        val lines = text.lines()
        val extracted = mutableListOf<String>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            // Check if line looks like a bullet or checklist item
            if (trimmed.startsWith("•") || trimmed.startsWith("-") || trimmed.startsWith("*") || trimmed.startsWith("☐") || trimmed.startsWith("[ ]")) {
                val cleaned = trimmed.replace(Regex("^([•\\-*☐\\[\\]\\s]+)"), "").trim()
                if (cleaned.length in 3..80) {
                    extracted.add(cleaned)
                }
            } else if (trimmed.matches(Regex("^\\d+[.)]\\s+.*"))) {
                val cleaned = trimmed.replace(Regex("^\\d+[.)]\\s+"), "").trim()
                if (cleaned.length in 3..80) {
                    extracted.add(cleaned)
                }
            }
        }

        // If no bullet lines, check for trigger words like "need to", "have to", "remember to"
        if (extracted.isEmpty()) {
            val sentenceSplit = text.split(Regex("[,.;\n]"))
            for (part in sentenceSplit) {
                val p = part.trim()
                val lower = p.lowercase()
                val match = when {
                    lower.contains("need to ") -> p.substring(lower.indexOf("need to ") + 8)
                    lower.contains("have to ") -> p.substring(lower.indexOf("have to ") + 8)
                    lower.contains("must ") -> p.substring(lower.indexOf("must ") + 5)
                    lower.contains("call ") -> p.substring(lower.indexOf("call "))
                    lower.contains("buy ") -> p.substring(lower.indexOf("buy "))
                    lower.contains("finish ") -> p.substring(lower.indexOf("finish "))
                    lower.contains("complete ") -> p.substring(lower.indexOf("complete "))
                    else -> null
                }
                if (match != null && match.trim().length in 4..60) {
                    extracted.add(match.trim().replaceFirstChar { it.uppercase() })
                }
            }
        }

        return extracted.distinct().take(6)
    }

    /**
     * Converts voice / raw bullet thoughts into an organized diary entry.
     */
    fun refineDiaryFromPoints(points: String): AIProcessedDiary {
        val clean = points.trim()
        val lower = clean.lowercase()

        val mood = when {
            lower.contains("happy") || lower.contains("great") || lower.contains("good day") || lower.contains("excited") -> Mood.HAPPY
            lower.contains("tired") || lower.contains("exhausted") || lower.contains("stressed") -> Mood.STRESSED
            lower.contains("sad") || lower.contains("down") || lower.contains("bad") -> Mood.SAD
            lower.contains("calm") || lower.contains("peaceful") || lower.contains("relax") -> Mood.CALM
            lower.contains("love") || lower.contains("friends") || lower.contains("family") -> Mood.LOVED
            else -> Mood.GREAT
        }

        val title = when {
            lower.contains("college") && lower.contains("friend") -> "A Good Day with Friends"
            lower.contains("work") || lower.contains("project") -> "Productive Strides"
            lower.contains("workout") || lower.contains("gym") -> "Pushing Forward"
            clean.lines().firstOrNull()?.isNotBlank() == true -> {
                val first = clean.lines().first().replace(Regex("^[•\\-*#\\s]+"), "").take(32)
                first.ifEmpty { "Daily Reflection" }
            }
            else -> "Reflections of the Day"
        }

        val refinedContent = buildString {
            append("Today was a meaningful day.\n\n")
            val bulletPoints = clean.lines().filter { it.isNotBlank() }
            if (bulletPoints.size > 1) {
                bulletPoints.forEach { pt ->
                    val cleanPt = pt.replace(Regex("^[•\\-*\\s]+"), "").trim()
                    append("• ").append(cleanPt).append("\n")
                }
                append("\nOverall, ended the day feeling ${mood.label.lowercase()} and grateful for the progress made.")
            } else {
                append(clean)
                append("\n\nReflecting on the day brings clarity and perspective.")
            }
        }

        val tags = buildString {
            append("#daily")
            if (lower.contains("college")) append(", #college")
            if (lower.contains("friend")) append(", #friends")
            if (lower.contains("project")) append(", #project")
            if (lower.contains("fitness") || lower.contains("workout")) append(", #fitness")
        }

        return AIProcessedDiary(
            suggestedTitle = title,
            refinedContent = refinedContent,
            suggestedMood = mood,
            suggestedTags = tags
        )
    }

    /**
     * Summarizes note content into structured takeaways.
     */
    fun summarizeNote(title: String, content: String): String {
        if (content.isBlank()) return "Note contains no text to summarize."
        val lines = content.lines().filter { it.isNotBlank() }
        val preview = lines.take(3).joinToString(" ")
        return "📝 **Summary of \"$title\"**:\n\n" +
                "• Key theme: ${preview.take(140)}...\n" +
                "• Length: ${content.length} characters across ${lines.size} paragraphs.\n" +
                "• Recommended action: Review key items and extract tasks if applicable."
    }

    private fun queryGeminiModel(prompt: String, apiKey: String): String? {
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val jsonPayload = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "You are LifeOS AI, a private personal life operating assistant. Answer concisely and politely: $prompt")
                            })
                        }
                        put("parts", parts)
                    })
                }
                put("contents", contents)
            }

            val body = jsonPayload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val resBody = response.body?.string() ?: return null
                val resJson = JSONObject(resBody)
                val candidates = resJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val contentObj = firstCandidate?.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                parts?.optJSONObject(0)?.optString("text")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
