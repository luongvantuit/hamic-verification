package org.hamic.hv

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import org.hamic.core.MerkleTree
import org.hamic.core.Sha256

class ResultHandlerActivity : AppCompatActivity() {

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var imgViewResult: ImageView

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
    }

    override fun onStart() {
        super.onStart()
        fireStore.collection("blockchain").whereEqualTo("block_hash", blockHash).get()
            .addOnCompleteListener { blockchainIt ->
                if (blockchainIt.isSuccessful) {
                    val blockChainDocuments = blockchainIt.result
                    if (blockChainDocuments == null || blockChainDocuments.size() != 1) {
                        this@ResultHandlerActivity.finish()
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
                                                this@ResultHandlerActivity.finish()
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
                                        this@ResultHandlerActivity.finish()
                                    }
                                }

                        }
                    }
                } else {
                    this@ResultHandlerActivity.finish()
                }
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
                    }
                }
        } else {
            finish()
        }
    }
}