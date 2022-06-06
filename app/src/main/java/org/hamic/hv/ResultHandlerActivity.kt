package org.hamic.hv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore

class ResultHandlerActivity : AppCompatActivity() {

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()


    private lateinit var id: String
    private lateinit var blockHash: String
    private lateinit var imgHash: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_handler)
        id = intent.getStringExtra("id")!!
        blockHash = intent.getStringExtra("blockHash")!!
        imgHash = intent.getStringExtra("imgHash")!!
    }

    fun verifyComplete() {

    }
}