package com.intellij.palg

import com.google.gson.GsonBuilder
import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.project.Project
import com.intellij.palg.model.ActivityData
import com.intellij.psi.PsiFile
import mu.KotlinLogging

class PalgListener : EditorFactoryListener, DocumentListener, CopyPastePreProcessor {

    private val logger = KotlinLogging.logger {}
    private val gson = GsonBuilder().disableHtmlEscaping().create()

    override fun editorCreated(event: EditorFactoryEvent) {
        println("editorCreated")
        println(event.editor)
    }

    override fun documentChanged(event: DocumentEvent) {
        println("documentChanged")
        val oldLength = event.oldLength
        val newLength = event.newLength
        val changedVirtualFileURL = PalgUtils.getVirtualFileIdFromActiveEditor(event.document) ?: ""

        if(changedVirtualFileURL.startsWith("https:")){
            return
        }

        if (newLength > oldLength) {
            if(event.newFragment.toString().startsWith("IntellijIdeaRulezzz")){
                return
            }
            val activityData = ActivityData(
                time = PalgUtils.getCurrentDateTime(),
                sequence = "TextInsert",
                text = event.newFragment.toString(),
                textWidgetClass = "CodeViewText",
                textWidgetId = PalgUtils.getVirtualFileIdFromActiveEditor(event.document) ?: "",
                index = PalgUtils.getIndex(event, event.offset)
            )
            logger.info { gson.toJson(activityData) }
        } else if (newLength < oldLength) {
            val activityData = ActivityData(
                time = PalgUtils.getCurrentDateTime(),
                sequence = "TextDelete",
                textWidgetClass = "CodeViewText",
                textWidgetId = PalgUtils.getVirtualFileIdFromActiveEditor(event.document) ?: "",
                index1 = PalgUtils.getIndex(event, event.offset),
                index2 = PalgUtils.getIndex2(event, event.offset)
            )
            logger.info { gson.toJson(activityData) }
        }
    }

    override fun preprocessOnCopy(file: PsiFile?, startOffsets: IntArray?, endOffsets: IntArray?, text: String?): String? = null

    override fun preprocessOnPaste(project: Project?, file: PsiFile?, editor: Editor?, text: String?, rawText: RawText?): String {
        println("preprocessOnPaste")
        val activityData = ActivityData(
            time = PalgUtils.getCurrentDateTime(),
            sequence = "<<Paste>>",
            textWidgetClass = "CodeViewText",
            textWidgetId = editor?.let { PalgUtils.getVirtualFileIdFromActiveEditor(it.document) } ?: ""
        )
        logger.info { gson.toJson(activityData) }
        return text ?: ""
    }


}