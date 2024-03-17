package com.palg.actions

import com.intellij.ide.actions.RevealFileAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import java.nio.file.Paths

class OpenLogFolderAction : DumbAwareAction(){
    override fun actionPerformed(event: AnActionEvent) {
        val tmpDir = System.getProperty("user.home")
        RevealFileAction.openDirectory(Paths.get("$tmpDir/PALG_logs"))
    }
}