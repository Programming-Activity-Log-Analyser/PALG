package com.intellij.palg

import com.google.gson.GsonBuilder
import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.palg.PalgUtils.Companion.getUUIDFromString
import com.intellij.palg.model.ActivityData
import com.intellij.psi.PsiFile
import mu.KotlinLogging


class PalgListener : FileEditorManagerListener, DocumentListener, CopyPastePreProcessor, ExecutionListener {

    private val logger = KotlinLogging.logger {}
    private val gson = GsonBuilder().disableHtmlEscaping().create()

    override fun documentChanged(event: DocumentEvent) {
        val oldLength = event.oldLength
        val newLength = event.newLength
        val changedVirtualFileURL = PalgUtils.getVirtualFileURLByDocument(event.document) ?: ""

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
                textWidgetId = PalgUtils.getVirtualFileUUIDByDocument(event.document),
                index = PalgUtils.getIndex(event, event.offset)
            )
            logger.info { gson.toJson(activityData) }
        } else if (newLength < oldLength) {
            val activityData = ActivityData(
                time = PalgUtils.getCurrentDateTime(),
                sequence = "TextDelete",
                textWidgetClass = "CodeViewText",
                textWidgetId = PalgUtils.getVirtualFileUUIDByDocument(event.document),
                index1 = PalgUtils.getIndex(event, event.offset),
                index2 = PalgUtils.getIndex2(event, event.offset)
            )
            logger.info { gson.toJson(activityData) }
        }
    }

    override fun preprocessOnCopy(file: PsiFile?, startOffsets: IntArray?, endOffsets: IntArray?, text: String?): String? = null

    override fun preprocessOnPaste(project: Project?, file: PsiFile?, editor: Editor?, text: String?, rawText: RawText?): String {
        val activityData = ActivityData(
            time = PalgUtils.getCurrentDateTime(),
            sequence = "<<Paste>>",
            textWidgetClass = "CodeViewText",
            textWidgetId = editor?.let { PalgUtils.getVirtualFileUUIDByDocument(it.document) } ?: ""
        )
        logger.info { gson.toJson(activityData) }
        return text ?: ""
    }

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        super.fileOpened(source, file)
        val activityData = ActivityData(
            time = PalgUtils.getCurrentDateTime(),
            sequence = "Open",
            textWidgetClass = "CodeViewText",
            textWidgetId = getUUIDFromString(file.url),
            filename = file.presentableUrl
        )
        logger.info { gson.toJson(activityData) }

        val activityDataTextInsert = ActivityData(
            time = PalgUtils.getCurrentDateTime(),
            sequence = "TextInsert",
            text = source.selectedTextEditor?.document?.text ?: "",
            textWidgetClass = "CodeViewText",
            textWidgetId = getUUIDFromString(file.url),
            index = "1.0"
        )
        logger.info { gson.toJson(activityDataTextInsert) }
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        super.fileClosed(source, file)
        val activityData = ActivityData(
            time = PalgUtils.getCurrentDateTime(),
            sequence = "Close",
            textWidgetClass = "CodeViewText",
            textWidgetId = getUUIDFromString(file.url),
            filename = file.presentableUrl
        )
        logger.info { gson.toJson(activityData) }
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        super.selectionChanged(event)
        val activityData = ActivityData(
            time = PalgUtils.getCurrentDateTime(),
            sequence = "<Button-1>",
            textWidgetClass = "CodeViewText",
            textWidgetId = event.newFile?.url?.let { getUUIDFromString(it) }
        )
        logger.info { gson.toJson(activityData) }
    }

    override fun processStarting(executorId: String, env: ExecutionEnvironment, handler: ProcessHandler) {
        super.processStarted(executorId, env, handler)
        val editor = FileEditorManager.getInstance(env.project).selectedTextEditor
        val file = editor?.let { PalgUtils.getVirtualFileByDocument(it.document) }
        val activityData = ActivityData(
            time = PalgUtils.getCurrentDateTime(),
            sequence = "ShellCommand",
            commandText =  "%${executorId} ${file?.name}\\n",
        )
        logger.info { gson.toJson(activityData) }
    }
}