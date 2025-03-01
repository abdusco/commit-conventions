package dev.abdus.commitconventions

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.LocalChangeList
import com.intellij.openapi.vcs.changes.ui.CommitMessageProvider
import com.intellij.vcs.commit.CommitMessageUi
import com.jetbrains.rd.util.printlnError
import git4idea.GitUtil
import java.util.regex.Pattern

class CommitMessageProvider : CommitMessageProvider {
    override fun getCommitMessage(changeList: LocalChangeList, project: Project): String? {
        // Get the existing commit message if any
        val commitMessage = changeList.comment

        // Get current branch name
        val branchName = getCurrentBranchName(project) ?: return commitMessage

        // Apply regex pattern
        val settings = CommitTemplateSettings.getInstance()
        return try {
            val pattern = Pattern.compile(settings.regexPattern)
            val matcher = pattern.matcher(branchName)

            if (matcher.find()) {
                var template = settings.messageTemplate
                for (i in 1..matcher.groupCount()) {
                    template = template.replace("$$i", matcher.group(i))
                }

                // Append to existing message or use as new message
                when {
                    commitMessage.isNullOrBlank() -> template
                    commitMessage.endsWith(template) -> commitMessage // Avoid duplicate templates
                    else -> "$commitMessage\n\n$template"
                }
            } else {
                commitMessage
            }
        } catch (e: Exception) {
            // In case of any error, return the original message
            commitMessage
        }
    }

    private fun getCurrentBranchName(project: Project): String? {
        GitUtil.getRepositories(project).forEach { repository ->
            repository.currentBranch?.let {
                return it.name
            }
        }
        return null
    }
}