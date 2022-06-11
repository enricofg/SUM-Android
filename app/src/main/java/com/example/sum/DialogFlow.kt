package com.example.sum

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sum.ui.bus.BusFragment
import com.example.sum.utility.mainViewModel.MainViewModel
import com.example.sum.utility.mainViewModel.MainViewModelFactory
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.put
import com.example.sum.utility.repository.Repository
import com.google.android.material.internal.ContextUtils.getActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class DialogFlow : AppCompatActivity() {


    private lateinit var ViewModel: MainViewModel
    private lateinit var Adress: Stop
    private lateinit var binding: DialogFlow
    private var couter = 0
    private var ArrayUser1: Array<String> = arrayOf("get bus schedule")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_flow)
        val dialog = findViewById<Button>(R.id.DialogButton)
        val text = findViewById<EditText>(R.id.DialogTextBox)
        val recicler = findViewById<RecyclerView>(R.id.dialogRecicler)
        recicler.layoutManager = LinearLayoutManager(this)
        recicler.setHasFixedSize(true)
        val adapter = GroupAdapter<GroupieViewHolder>()
        val viewModelFactory = MainViewModelFactory(Repository())
        ViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        ViewModel.getStops()
        ViewModel.stops.observe(this, Observer { response ->

            if (response.isSuccessful) {

                Adress = response.body()!!
                Log.d("address", Adress.toString())
            }
        })

        dialog.setOnClickListener {
                var mgs=""
            if (text.text.toString().isNotEmpty()) {
                adapter.add(ChatItemUser(text.text.toString()))
                recicler.adapter = adapter
                if (text.text.toString()[text.text.length - 1] == ' ') {
                    mgs = text.text.substring(0, text.text.length - 1)
                }else {
                    mgs = text.text.toString()
                }
                this.DialogMessage(adapter, recicler, mgs);
                text.text.clear()
            }
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }


    }


    private fun DialogMessage(adapter: GroupAdapter<GroupieViewHolder>, recicler: RecyclerView, mgs:String) {

        if (couter == 0 && ArrayUser1.contains(mgs.lowercase())){


            adapter.add(chatItemDialog("Nearby Stations:"))
            recicler.adapter = adapter


           // val intent = Intent(this, MainActivity::class.java)
            /*intent.putExtra("addressTime",Adress[0].Schedule_Time)
            intent.putExtra("addressName",Adress[0].Schedule_Time)
            startActivity(intent)*/
            Adress.forEach {
                adapter.add(chatItemDialog(it.Stop_Name))
                recicler.adapter = adapter
            }

            couter++
        }else if(couter == 1 && mgs.lowercase()!="exit"){
            Log.d("address", mgs)
            Adress.forEach {
                if(it.Stop_Name.lowercase() == mgs.lowercase()){
                    Log.d("address", mgs)
                    val sharedPreference =  this.getSharedPreferences("SCHEDULE",Context.MODE_PRIVATE)

                    sharedPreference.apply {
                        put("ScheduleTime",it.Schedule_Time)
                        put("ScheduleName",it.Stop_Name)
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

            }
        }else if(mgs.lowercase()=="exit"){

            couter = 0

        } else{
            adapter.add(chatItemDialog("please repeat "))
        }





    }
}
class ChatItemUser(val value:String) :Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.userMessage).text = value
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_user
    }

}

class chatItemDialog(val value:String) :Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.DialogMessage).text = value
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_dialog
    }

}