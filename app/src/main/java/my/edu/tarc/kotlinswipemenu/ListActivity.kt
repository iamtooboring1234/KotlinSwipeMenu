package my.edu.tarc.kotlinswipemenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.kotlinswipemenu.Model.Insurance
import java.text.SimpleDateFormat

class ListActivity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("Testing")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

    }




}