package com.adytransjaya.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adytransjaya.app.ui.AdyTransJayaApp
import com.adytransjaya.app.ui.AppViewModel
import com.adytransjaya.app.ui.theme.AdyTransJayaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdyTransJayaTheme {
                val appViewModel: AppViewModel = viewModel(
                    factory = AppViewModel.Factory(applicationContext),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics { testTagsAsResourceId = true },
                ) {
                    AdyTransJayaApp(viewModel = appViewModel)
                }
            }
        }
    }
}
