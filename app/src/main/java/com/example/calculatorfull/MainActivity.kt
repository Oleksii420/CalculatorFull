package com.example.calculatorfull

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculatorfull.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var firstNumber: Double = 0.0
    private var operation: String? = null
    private var isNewInput: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val digitButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )
        digitButtons.forEach { button ->
            button.setOnClickListener { appendDigit(button.text.toString()) }
        }

        binding.btnClear.setOnClickListener { clearAll() }

        binding.btnPlus.setOnClickListener { onOperationPressed("+") }
        binding.btnMinus.setOnClickListener { onOperationPressed("-") }
        binding.btnMultiply.setOnClickListener { onOperationPressed("*") }
        binding.btnDivide.setOnClickListener { onOperationPressed("/") }
        binding.btnEquals.setOnClickListener { onEqualsPressed() }
        binding.btnDot.setOnClickListener { appendDot() }
        binding.btnBackspace.setOnClickListener { deleteLast() }
    }

    private fun appendDigit(digit: String) {
        val current = binding.tvDisplay.text.toString()
        if (isNewInput || current == "0") {
            binding.tvDisplay.text = digit
            isNewInput = false
        } else {
            binding.tvDisplay.text = current + digit
        }
    }

    private fun clearAll() {
        binding.tvDisplay.text = "0"
        firstNumber = 0.0
        operation = null
        isNewInput = true
    }

    private fun calculate(a: Double, b: Double, op: String): Double? {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b == 0.0) null else a / b
            else -> null
        }
    }

    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
    }

    private fun showError() {
        binding.tvDisplay.text = "Помилка"
        operation = null
        isNewInput = true
    }

    private fun appendDot() {
        val current = binding.tvDisplay.text.toString()
        if (isNewInput) {
            binding.tvDisplay.text = "0."
            isNewInput = false
        } else if (!current.contains(".")) {
            binding.tvDisplay.text = current + "."
        }
    }

    private fun deleteLast() {
        if (isNewInput) return
        val shortened = binding.tvDisplay.text.toString().dropLast(1)
        binding.tvDisplay.text = if (shortened.isEmpty()) "0" else shortened
    }

    private fun onOperationPressed(op: String) {
        val current = binding.tvDisplay.text.toString().toDoubleOrNull() ?: 0.0
        val pending = operation

        if (pending != null && !isNewInput) {
            val result = calculate(firstNumber, current, pending)
            if (result == null) {
                showError()
                return
            }
            firstNumber = result
            binding.tvDisplay.text = formatNumber(result)
        } else {
            firstNumber = current
        }

        operation = op
        isNewInput = true
    }

    private fun onEqualsPressed() {
        val op = operation ?: return
        val secondNumber = binding.tvDisplay.text.toString().toDoubleOrNull() ?: return

        val result = calculate(firstNumber, secondNumber, op)
        if (result == null) {
            showError()
            return
        }
        binding.tvDisplay.text = formatNumber(result)
        operation = null
        isNewInput = true
    }
}