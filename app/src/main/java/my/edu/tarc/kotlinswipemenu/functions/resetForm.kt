package my.edu.tarc.kotlinswipemenu.functions

import android.view.ViewGroup
import android.widget.*

class resetForm {
    fun resetAllField(group: ViewGroup) {
        var i = 0
        val count = group.childCount
        while (i < count) {
            val view = group.getChildAt(i)
            if (view is EditText) {
                view.text.clear()
            }
            if (view is RadioGroup) {
                (view.getChildAt(0) as RadioButton).isChecked = true
            }
            if (view is Spinner) {
                view.setSelection(0)
            }
            if (view is CheckBox) {
                if(view.isChecked) {
                    view.toggle()
                }
            }
            if (view is ViewGroup && view.childCount > 0) resetAllField(view)
            ++i
        }
    }
}