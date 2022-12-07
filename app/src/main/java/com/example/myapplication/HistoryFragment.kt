package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoryFragment:Fragment(R.layout.fragment_history) {
    private lateinit var editUserText : TextView
    private lateinit var editNumCaches : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editUserText = view.findViewById(R.id.diplayUserName)
        editNumCaches = view.findViewById(R.id.displayNumFound)

        val userdB = Firebase.auth.currentUser?.email.toString().split("@")[0]
        val db = Firebase.firestore



        db.collection("caches")
            .get()
            .addOnSuccessListener { result ->
                    editNumCaches.text = result.size().toString()
                    editUserText.text = userdB.toString()
                }

    }


}