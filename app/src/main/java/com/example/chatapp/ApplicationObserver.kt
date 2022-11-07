package com.example.chatapp

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ApplicationObserver : LifecycleObserver {
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    val mDbRef = FirebaseDatabase.getInstance("https://metischat-default-rtdb.europe-west1.firebasedatabase.app").getReference()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createSomething() {
        mDbRef.child("user").child(currentUser!!).child("online")
            .setValue("True")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startSomething() {
        mDbRef.child("user").child(currentUser!!).child("online")
            .setValue("True")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopSomething() {
        mDbRef.child("user").child(currentUser!!).child("online")
            .setValue("False")
    }
}