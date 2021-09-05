package my.edu.tarc.kotlinswipemenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import my.edu.tarc.kotlinswipemenu.Adapter.InsuranceAdapter
import my.edu.tarc.kotlinswipemenu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var adapter : InsuranceAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}

