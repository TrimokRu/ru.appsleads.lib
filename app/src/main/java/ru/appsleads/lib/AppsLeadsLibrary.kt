package ru.appsleads.lib

import android.content.Context
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

private val AID = "AID"
private val CAMPAIGN = "campaign"

class AppsLeadsLibrary(private val context: Context, private val appsFlayerKey: String):
    IAppsLeadsLibrary {

    override fun init(
        onConversionDataSuccess: (sub: String?) -> Unit,
        onConversionDataFail: () -> Unit,
        onAppOpenAttribution: () -> Unit,
        onAttributionFailure: () -> Unit
    ) {
        AppsFlyerLib.getInstance().init(appsFlayerKey, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversinon: MutableMap<String, Any>?)= onConversionDataSuccess(getCampaignStringFromMutableMap(conversinon))

            override fun onConversionDataFail(p0: String?) = onConversionDataFail()

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) = onAppOpenAttribution()

            override fun onAttributionFailure(p0: String?) = onAttributionFailure()
        }, context)
    }

    override fun getCampaignStringFromMutableMap(mutableMap: MutableMap<String, Any>?): String? {
        return if (mutableMap?.contains(CAMPAIGN) == true) {
            var sub = "?$AID=${AppsFlyerLib.getInstance().getAppsFlyerUID(context).toString()}"
            mutableMap[CAMPAIGN].toString().split("_")
                .mapIndexed { index, item -> sub += "&sub${index + 1}=$item" }
            sub
        } else null
    }


    @Composable
    override fun Browser(url: String, onFailureLoad: () -> Unit) {
        var webView: WebView? = null

        BackHandler(true) {
            if (webView?.canGoBack() == true) webView!!.goBack()
        }

        AndroidView(modifier = Modifier.fillMaxSize(), factory = {
            WebView(it).apply {

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        CookieManager.getInstance().flush()
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) = onFailureLoad()


                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) = onFailureLoad()

                }
                loadUrl(url)
                webView = this
            }
        })
    }
}