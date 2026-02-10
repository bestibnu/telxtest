package com.telxtest.android

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.telxtest.android.databinding.ActivityMainBinding
import com.telxtest.android.network.ApiClient
import com.telxtest.android.network.CallRequest
import kotlinx.coroutines.launch
import android.webkit.JavascriptInterface

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val webAppUrl: String by lazy { getString(R.string.web_app_url) }
    private val servicesFragment: String by lazy { getString(R.string.web_services_fragment) }
    private val accountFragment: String by lazy { getString(R.string.web_account_fragment) }
    private var lastWebUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWebView()
        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState)
            lastWebUrl = binding.webView.url
        }
        showWeb()

        binding.placeCallButton.setOnClickListener {
            placeCall()
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showNative()
                    true
                }
                R.id.nav_services,
                R.id.nav_account -> {
                    showWeb()
                    val fragment = if (item.itemId == R.id.nav_services) {
                        servicesFragment
                    } else {
                        accountFragment
                    }
                    loadWebSection(fragment)
                    true
                }
                else -> false
            }
        }

        binding.bottomNav.selectedItemId = R.id.nav_account

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.visibility == View.VISIBLE && binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun placeCall() {
        val from = binding.fromInput.text?.toString().orEmpty()
        val to = binding.destinationInput.text?.toString().orEmpty()
        if (from.isBlank() || to.isBlank()) {
            showStatus("Enter your number and destination.")
            return
        }
        lifecycleScope.launch {
            runCatching {
                ApiClient.api.placeCall(CallRequest(from, to))
            }.onSuccess {
                showStatus("Call queued: ${it.from} -> ${it.to}")
            }.onFailure {
                showStatus("Place call failed: ${it.message}")
            }
        }
    }

    private fun showStatus(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupWebView() {
        with(binding.webView) {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            webViewClient = WebViewClient()
            addJavascriptInterface(TelxWebBridge(), "TelxNative")
        }
    }

    private fun showWeb() {
        binding.nativeScroll.visibility = View.GONE
        binding.webView.visibility = View.VISIBLE
        loadWebSection("")
    }

    private fun showNative() {
        binding.webView.visibility = View.GONE
        binding.nativeScroll.visibility = View.VISIBLE
    }

    private fun loadWebSection(fragment: String) {
        val sanitized = fragment.trim()
        val url = if (sanitized.isBlank()) {
            webAppUrl
        } else if (sanitized.startsWith("#")) {
            "$webAppUrl$sanitized"
        } else {
            "$webAppUrl#$sanitized"
        }
        if (lastWebUrl != url) {
            binding.webView.loadUrl(url)
            lastWebUrl = url
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    private inner class TelxWebBridge {
        @JavascriptInterface
        fun onLoggedIn() {
            runOnUiThread {
                binding.bottomNav.selectedItemId = R.id.nav_home
                showNative()
            }
        }
    }
}
