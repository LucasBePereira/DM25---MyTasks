package com.lucas.mytasks.entity

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class TaskStatus {
    OVERDUE,   // Atrasada
    DUE_TODAY, // Vence Hoje
    COMPLETED, // Concluída
    IN_PROGRESS// Em Andamento (padrão)
}
data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val completed: Boolean = false
) : Serializable {

    fun formatDate(): String {
        return date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
    }

    fun formatTime(): String {
        return time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
    }

    fun formatDateTime(): String {
        val formattedDate = formatDate()
        val formattedTime = formatTime()
        return when {
            date != null && time != null -> "$formattedDate $formattedTime"
            date != null -> formattedDate
            time != null -> formattedTime
            else -> "-"
        }
    }

    fun getStatus(): TaskStatus {
        // A prioridade máxima é o status "Concluída"
        if (completed) {
            return TaskStatus.COMPLETED
        }

        // Se não houver data definida, a tarefa está simplesmente em andamento
        if (date == null) {
            return TaskStatus.IN_PROGRESS
        }

        val today = LocalDate.now()

        // Compara a data da tarefa com a data de hoje
        return when {
            date.isBefore(today) -> TaskStatus.OVERDUE   // Data já passou -> Atrasada
            date.isEqual(today) -> TaskStatus.DUE_TODAY  // Data é hoje -> Vence Hoje
            else -> TaskStatus.IN_PROGRESS             // Data é no futuro -> Em Andamento
        }
    }
}
