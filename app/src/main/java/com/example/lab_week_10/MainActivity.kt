package com.example.lab_week_10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.viewmodels.TotalViewModel
import com.example.lab_week_10.database.Total
import java.util.Date
import android.widget.Toast
import com.example.lab_week_10.database.TotalObject


class MainActivity : AppCompatActivity() {
    private val db by lazy { prepareDatabase() }

    override fun onStart() {
        super.onStart()

        val result = db.totalDao().getTotal(ID)
        if (result.isNotEmpty()) {
            val date = result.first().total.date
            Toast.makeText(this, "Last updated: $date", Toast.LENGTH_LONG).show()
        }
    }


    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeValueFromDatabase()
        prepareViewModel()
    }


    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {

        // Observe LiveData
        viewModel.total.observe(this) { total ->
            updateText(total)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total-database"
        ).allowMainThreadQueries().build()
    }
    private fun initializeValueFromDatabase() {
        val result = db.totalDao().getTotal(ID)

        if (result.isEmpty()) {
            db.totalDao().insert(
                Total(
                    id = 1,
                    total = TotalObject(
                        value = 0,
                        date = Date().toString()
                    )
                )
            )
        } else {
            val obj = result.first().total
            viewModel.setTotal(obj.value, obj.date)
        }
    }

    override fun onPause() {
        super.onPause()

        val newValue = viewModel.total.value ?: 0
        val newDate = Date().toString()

        db.totalDao().update(
            Total(
                ID,
                TotalObject(newValue, newDate)
            )
        )
    }


    companion object {
        const val ID: Long = 1
    }

}
