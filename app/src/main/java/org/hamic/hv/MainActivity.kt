package org.hamic.hv


import android.os.Bundle
import org.hamic.internal.IActivity

class MainActivity : IActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}