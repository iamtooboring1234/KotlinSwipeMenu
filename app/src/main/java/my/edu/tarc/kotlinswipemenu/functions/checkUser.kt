package my.edu.tarc.kotlinswipemenu.functions

import com.google.firebase.auth.FirebaseAuth

class checkUser() {
    private val auth = FirebaseAuth.getInstance()

    fun ifCurrentUserExists():Boolean{
        if(auth.currentUser == null){
            return false
        }
        return true
    }

    fun getCurrentUserUID():String? {
        return auth.currentUser?.uid
    }
}