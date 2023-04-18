package com.intellij.palg.model

import com.google.gson.annotations.SerializedName

data class ActivityData(
    @SerializedName("time") val time: String,
    @SerializedName("sequence") val sequence: String,
    @SerializedName("text_widget_id") val textWidgetId: String? = null,
    @SerializedName("text_widget_class") val textWidgetClass: String? = null,
    @SerializedName("filename") val filename: String? = null,
    @SerializedName("index") val index: String? = null,
    @SerializedName("index1") val index1: String? = null,
    @SerializedName("index2") val index2: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("command_text") val commandText: String? = null,
)
