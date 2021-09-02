package my.edu.tarc.kotlinswipemenu

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.kotlinswipemenu.Adapter.myAdapter
import my.edu.tarc.kotlinswipemenu.Helper.MyButton
import my.edu.tarc.kotlinswipemenu.Helper.MySwipeHelper
import my.edu.tarc.kotlinswipemenu.Listener.MyButtonClickListener
import my.edu.tarc.kotlinswipemenu.Model.Item
import my.edu.tarc.kotlinswipemenu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var adapter : myAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.recyclerTest.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        binding.recyclerTest.layoutManager = layoutManager



        val swipe = object: MySwipeHelper(this, binding.recyclerTest, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(this@MainActivity, "Delete", 30, R.drawable.ic_baseline_delete_24, Color.parseColor("#FF3C30"),
                object: MyButtonClickListener{
                    override fun onClick(pos: Int) {
                        Toast.makeText(this@MainActivity, "DELETE ID$pos", Toast.LENGTH_SHORT).show()
                    }
                }))
                buffer.add(MyButton(this@MainActivity, "Update", 30, R.drawable.ic_baseline_edit_24, Color.parseColor("#FF3C30"),
                object: MyButtonClickListener{
                    override fun onClick(pos: Int) {
                        Toast.makeText(this@MainActivity, "UPDATE ID$pos", Toast.LENGTH_SHORT).show()
                    }
                }))
            }

        }





    }


}