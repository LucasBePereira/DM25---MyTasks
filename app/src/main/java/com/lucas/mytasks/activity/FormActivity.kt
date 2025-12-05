package com.lucas.mytasks.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lucas.mytasks.R
import com.lucas.mytasks.databinding.ActivityFormBinding
import com.lucas.mytasks.entity.Task
import com.lucas.mytasks.extension.hasValue
import com.lucas.mytasks.extension.value
import com.lucas.mytasks.service.TaskService
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding

    private val taskService: TaskService by viewModels()

    private var taskId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        intent.extras?.getSerializable("task")?.let { extra ->
//            val task = extra as Task
//
//            taskId = task.id
//            binding.etTitle.setText(task.title)
//            binding.etDescription.setText(task.description)
//            binding.etDate.setText(task.formatDate())
//            binding.etTime.setText(task.formatTime())
//        }

        intent.extras?.getString(Intent.EXTRA_TEXT)?.let { text ->
            binding.etTitle.setText(text)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        initComponents()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveOrUpdateTask(task: Task) {
        val operation = if (task.id == null) {
            taskService.create(task)
        } else {
            taskService.update(task)
        }

        operation.observe(this) { response ->
            if (response.error) {
                val message = if (task.id == null) R.string.create_error else R.string.update_error
                showAlert(message)
            } else {
                finish()
            }
        }
    }

    private fun initComponents() {
        // 1. Configura os cliques nos campos de data e hora para abrir os seletores
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.etTime.setOnClickListener {
            showTimePicker()
        }

        // 2. Configura o clique do botão Salvar
        binding.btSave.setOnClickListener {
            // Limpa os erros anteriores
            binding.layoutTitle.error = null
            binding.layoutDate.error = null
            binding.layoutTime.error = null

            val title = binding.etTitle.value()

            // Valida se o título não está em branco
            if (title.isBlank()) {
                binding.layoutTitle.error = getString(R.string.title_required)
                return@setOnClickListener // Para a execução
            }

            val dateText = binding.etDate.value()
            val timeText = binding.etTime.value()
            var date: LocalDate? = null
            var time: LocalTime? = null

            // Tenta converter a data de forma segura (necessário caso o usuário edite o campo)
            if (dateText.isNotBlank()) {
                try {
                    date = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } catch (e: Exception) {
                    binding.layoutDate.error = "Data inválida (use dd/MM/aaaa)"
                    return@setOnClickListener
                }
            }

            // Tenta converter a hora de forma segura
            if (timeText.isNotBlank()) {
                try {
                    time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"))
                } catch (e: Exception) {
                    binding.layoutTime.error = "Hora inválida (use HH:mm)"
                    return@setOnClickListener
                }
            }

            // Se todas as validações passaram, cria o objeto Task
            val task = Task(
                id = taskId,
                title = title,
                description = binding.etDescription.value(),
                date = date,
                time = time
            )

            // Chama a função auxiliar para salvar ou atualizar
            saveOrUpdateTask(task)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                // Formata a data e a define no EditText
                binding.etDate.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = LocalTime.of(hourOfDay, minute)
                // Formata a hora e a define no EditText
                binding.etTime.setText(selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // true para usar o formato 24h
        )
        timePicker.show()
    }

    private fun showAlert(message: Int) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .create()
                .show()
    }
}