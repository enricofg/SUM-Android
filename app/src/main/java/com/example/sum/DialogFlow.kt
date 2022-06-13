package com.example.sum

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sum.utility.mainViewModel.MainViewModel
import com.example.sum.utility.mainViewModel.MainViewModelFactory
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.put
import com.example.sum.utility.repository.Repository
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.util.*

class DialogFlow : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var address: Stop
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    private lateinit var recycler: RecyclerView
    private var counter = 0
    private var options: Array<String> =
        arrayOf("get bus schedule", "dá-me o horário dos autocarros")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_flow)
        val dialog = findViewById<Button>(R.id.DialogButton)
        val text = findViewById<EditText>(R.id.DialogTextBox)
        val btnSpeak = findViewById<ImageButton>(R.id.btnSpeak)

        recycler = findViewById<RecyclerView>(R.id.dialogRecicler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        adapter = GroupAdapter<GroupieViewHolder>()
        val viewModelFactory = MainViewModelFactory(Repository())
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.getStops()
        viewModel.stops.observe(this, Observer { response ->
            if (response.isSuccessful) {
                address = response.body()!!
                Log.d("address", address.toString())
            }
        })

        dialog.setOnClickListener {
            lateinit var mgs: String
            if (text.text.toString().isNotEmpty()) {
                adapter.add(ChatItemUser(text.text.toString()))
                recycler.adapter = adapter
                mgs = if (text.text.toString()[text.text.length - 1] == ' ') {
                    text.text.substring(0, text.text.length - 1)
                } else {
                    text.text.toString()
                }
                this.DialogMessage(adapter, recycler, mgs);
                text.text.clear()
            }
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        btnSpeak.setOnClickListener {


            getSpeechInput()
        }
    }

    private fun getSpeechInput() {
        val intent = Intent(
            RecognizerIntent
                .ACTION_RECOGNIZE_SPEECH
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 10)
        } else {
            Toast.makeText(
                this,
                "Your Device Doesn't Support Speech Input",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode, data
        )
        when (requestCode) {
            10 -> if (resultCode == RESULT_OK &&
                data != null
            ) {
                val result =
                    data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )
                Log.d("result", result.toString())

                adapter.add(ChatItemUser(result?.get(0) ?: ""))
                recycler.adapter = adapter
                this.DialogMessage(adapter, recycler, result?.get(0) ?: "");
            }
        }
    }

    private fun DialogMessage(
        adapter: GroupAdapter<GroupieViewHolder>,
        recicler: RecyclerView,
        mgs: String
    ) {
        if (counter == 0 && options.contains(mgs.lowercase())) {
            adapter.add(chatItemDialog("Nearby Stations:"))
            recicler.adapter = adapter
            address.forEach {
                adapter.add(chatItemDialog(it.Stop_Name))
                recicler.adapter = adapter
            }
            counter++
        } else if (counter == 1 && mgs.lowercase() != "exit") {
            Log.d("address", mgs)
            address.forEach {
                if (it.Stop_Name.lowercase() == mgs.lowercase()) {
                    Log.d("address", mgs)
                    val sharedPreference =
                        this.getSharedPreferences("SCHEDULE", Context.MODE_PRIVATE)

                    sharedPreference.apply {
                        put("ScheduleTime", it.Stop_Id)
                        put("ScheduleName", it.Stop_Name)
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        } else if (mgs.lowercase() == "exit") {
            counter = 0
        } else {
            adapter.add(chatItemDialog("please repeat "))
        }
    }
}

class ChatItemUser(val value: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.userMessage).text = value
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_user
    }
}


class chatItemDialog(val value: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.DialogMessage).text = value
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_dialog
    }
}