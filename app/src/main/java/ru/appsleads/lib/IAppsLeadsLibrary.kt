package ru.appsleads.lib

import androidx.compose.runtime.Composable

interface IAppsLeadsLibrary {
    fun init(
        onConversionDataSuccess: (sub: String?) -> Unit,
        onConversionDataFail: () -> Unit,
        onAppOpenAttribution: () -> Unit,
        onAttributionFailure: () -> Unit
    )

    fun getCampaignStringFromMutableMap(mutableMap: MutableMap<String, Any>?): String?

    @Composable
    fun Browser(url: String, onFailureLoad: () -> Unit)
}