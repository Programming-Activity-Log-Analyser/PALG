package com.intellij.palg

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PalgUtils {
    companion object {
        fun getCurrentDateTime(): String {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            return currentDateTime.format(formatter)
        }

        fun getVirtualFileUUIDByDocument(document: Document): String {
            val virtualFile: VirtualFile? = FileDocumentManager.getInstance().getFile(document)
            return getUUIDFromString(virtualFile?.url ?: "")
        }

        fun getVirtualFileURLByDocument(document: Document): String? {
            return FileDocumentManager.getInstance().getFile(document)?.presentableUrl
        }

        fun getIndex(event: DocumentEvent, offset: Int): String{
            val text = event.document.getText(TextRange(0, offset))
            return getIndexFromString(text, offset)
        }

        fun getIndex2(event: DocumentEvent, offset: Int): String{
            val text = event.document.getText(TextRange(0, offset)) + event.oldFragment
            return getIndexFromString(text, text.length)
        }

        private fun getIndexFromString(text: String, offset: Int): String{
            val lineNum = text.count { it == '\n' } + 1
            val charPos = offset - text.lastIndexOf('\n') - 1
            return "$lineNum.$charPos"
        }

        fun getUUIDFromString(str: String): String {
            return UUID.nameUUIDFromBytes(str.toByteArray()).toString()
        }
    }
}