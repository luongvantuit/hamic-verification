package org.hamic.hv

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import org.hamic.core.MerkleTree
import org.hamic.core.Sha256

class ResultHandlerActivity : AppCompatActivity() {

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var imgViewResult: ImageView
    private lateinit var progressBarHandler: ProgressBar
    private lateinit var goToHome: AppCompatButton

    private lateinit var id: String
    private lateinit var blockHash: String
    private lateinit var imgHash: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_handler)
        id = intent.getStringExtra("id")!!
        blockHash = intent.getStringExtra("blockHash")!!
        imgHash = intent.getStringExtra("imgHash")!!

        imgViewResult = findViewById(R.id.image_view_result)
        progressBarHandler = findViewById(R.id.progress_bar_handler)
        goToHome = findViewById(R.id.button_go_to_home)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))




        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        fireStore.collection("blockchain").whereEqualTo("block_hash", blockHash).get()
            .addOnCompleteListener { blockchainIt ->
                if (blockchainIt.isSuccessful) {
                    val blockChainDocuments = blockchainIt.result
                    if (blockChainDocuments == null || blockChainDocuments.size() != 1) {
                        verifyComplete(false)
                    } else {
                        for (blockChainDocument in blockChainDocuments) {
                            val merkleProofIds: IntArray = MerkleTree.getMerkleProof(id.toInt())
                            fireStore.collection(
                                blockChainDocument.get("merkle_root_hash")?.toString()!!
                            ).get()
                                .addOnCompleteListener { merkleRootIt ->
                                    if (merkleRootIt.isSuccessful) {
                                        val merkleRootDocuments = merkleRootIt.result
                                        for (id in merkleProofIds) {
                                            var merkleRoot: QueryDocumentSnapshot? = null
                                            for (merkleRootDocument in merkleRootDocuments!!) {
                                                if (merkleRootDocument.id.toInt() == id) {
                                                    merkleRoot = merkleRootDocument
                                                }
                                            }
                                            if (merkleRoot == null) {
                                                verifyComplete(false)
                                            }
                                            imgHash =
                                                if (merkleRoot!!.id.toInt() % 2 == 0) {
                                                    Sha256.calHash(
                                                        imgHash + merkleRoot.get("node_hash")
                                                    )
                                                } else {
                                                    Sha256.calHash(
                                                        merkleRoot.get("node_hash")
                                                            .toString() + imgHash
                                                    )
                                                }
                                        }
                                        verifyComplete(
                                            imgHash == blockChainDocument.get("merkle_root_hash")
                                                .toString()
                                        )
                                    } else {
                                        verifyComplete(false)
                                    }
                                }
                        }
                    }
                } else {
                    verifyComplete(false)
                }
            }
        goToHome.setOnClickListener {
            val intent: Intent = Intent(this@ResultHandlerActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun verifyComplete(result: Boolean) {
        if (result) {
            fireStore.collection("base64_data").document(intent.getStringExtra("imgHash")!!).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val data: String = it.result!!.get("b64_text").toString()
                        val byteData: ByteArray = Base64.decode(data, Base64.DEFAULT)
                        val bitmap: Bitmap =
                            BitmapFactory.decodeByteArray(byteData, 0, byteData.size)
                        imgViewResult.setImageBitmap(bitmap)
                        progressBarHandler.visibility = View.GONE
                        imgViewResult.visibility = View.VISIBLE
                        goToHome.visibility = View.VISIBLE
                        Toast.makeText(this@ResultHandlerActivity, "Verify is success!", Toast.LENGTH_SHORT).show()
                    }
                }

        } else {
            Toast.makeText(this@ResultHandlerActivity, "Error! Can't verify.", Toast.LENGTH_LONG).show()
            Handler().postDelayed({
                finish()
            }, 3000)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}