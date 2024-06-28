package org.jyutping.jyutping

import android.content.Intent
import android.inputmethodservice.InputMethodService
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher

abstract class LifecycleInputMethodService: InputMethodService(), LifecycleOwner {

        protected val dispatcher = ServiceLifecycleDispatcher(this)

        @CallSuper
        override fun onCreate() {
                dispatcher.onServicePreSuperOnCreate()
                super.onCreate()
        }

        override fun onBindInput() {
                super.onBindInput()
                dispatcher.onServicePreSuperOnBind()
        }

        @CallSuper
        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
                return super.onStartCommand(intent, flags, startId)
        }

        @CallSuper
        override fun onDestroy() {
                dispatcher.onServicePreSuperOnDestroy()
                super.onDestroy()
        }
}
