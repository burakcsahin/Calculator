package com.example.calculator

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    lateinit var workingsTV: TextView
    lateinit var resultsTV: TextView

    private var workings: String = ""
    private var canAddOperation = false
    private var canAddDecimal = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        workingsTV = findViewById(R.id.workingsTV)
        workingsTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 61F)
        resultsTV = findViewById(R.id.resultsTV)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun backspaceAction(view: View) {
        workingsTV.text = workingsTV.text.dropLast(1)
    }

    fun allClearAction(view: View) {
        workingsTV.text = ""
        resultsTV.text = ""
    }

    fun equalsAction(view: View) {
        val result = calculateResult()
        if (result.isNotEmpty()) {
            resultsTV.text = DecimalFormat("#.####").format(result.toFloat())
        }
    }

    private fun calculateResult(): String {
        val digitsAndOperators = parseDigitsAndOperators()
        if (digitsAndOperators.isEmpty()) {
            return ""
        }
        val timesDivided = calculateTimesDivided(digitsAndOperators)
        if (timesDivided.isEmpty()) {
            return ""
        }

        return calculateMinusPlus(timesDivided).toString()
    }

    private fun calculateMinusPlus(paramList: MutableList<Any>): Any {
        var result = paramList[0] as Float
        for (i in paramList.indices) {
            if (paramList[i] is Char && i != paramList.lastIndex) {
                val operator = paramList[i]
                val nextDigit = paramList[i + 1] as Float
                when (operator) {
                    '+' -> result += nextDigit
                    '-' -> result -= nextDigit
                }
            }
        }
        return result
    }

    private fun calculateTimesDivided(paramList: MutableList<Any>): MutableList<Any> {
        var mutableList = paramList
        while (mutableList.contains('*') || mutableList.contains('/')) {
            mutableList = divideTimes(mutableList)
        }
        return mutableList
    }

    private fun divideTimes(paramList: MutableList<Any>): MutableList<Any> {
        val result = mutableListOf<Any>()
        var index = 0

        while (index < paramList.size) {
            val currentItem = paramList[index]
            if (currentItem is Float) {
                result.add(currentItem)
                index++
            } else if (currentItem is Char) {
                if (currentItem == '*') {
                    val previousDigit = result.removeAt(result.size - 1) as Float
                    val nextDigit = paramList[index + 1] as Float
                    val operationResult = previousDigit * nextDigit
                    result.add(operationResult)
                    index += 2
                } else if (currentItem == '/') {
                    val previousDigit = result.removeAt(result.size - 1) as Float
                    val nextDigit = paramList[index + 1] as Float
                    val operationResult = previousDigit / nextDigit
                    result.add(operationResult)
                    index += 2
                } else {
                    result.add(currentItem)
                    index++
                }
            }
        }
        return result
    }

    private fun parseDigitsAndOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (digit in workingsTV.text) {
            if (digit.isDigit() || digit == '.') {
                currentDigit += digit
            } else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(digit)
            }
        }

        if (currentDigit != "") {
            list.add(currentDigit.toFloat())
        }

        return list
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal) {
                    workingsTV.append(view.text)
                }
                canAddDecimal = false
            } else {
                workingsTV.append(view.text)
                canAddOperation = true
            }
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }
}