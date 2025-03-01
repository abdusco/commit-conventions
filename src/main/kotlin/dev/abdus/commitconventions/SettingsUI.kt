package dev.abdus.commitconventions


import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.Label
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import git4idea.GitUtil
import javax.swing.*
import java.awt.FlowLayout
import java.awt.Insets
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class CommitTemplateSettingsConfigurable : Configurable {
    private var regexPatternField: JBTextField? = null
    private var messageTemplateField: JTextArea? = null
    private var previewLabel: JTextArea? = null

    override fun getDisplayName(): String = "Commit Template Settings"

    override fun createComponent(): JComponent {
        regexPatternField = JBTextField()
        messageTemplateField = JBTextArea(5, 30)
        messageTemplateField?.lineWrap = true
        messageTemplateField?.wrapStyleWord = true
        messageTemplateField?.margin = JBUI.insets(5)

        previewLabel = JBTextArea(5, 30)
        previewLabel?.margin = JBUI.insets(5)
        previewLabel?.isEditable = false
        previewLabel?.lineWrap = true
        previewLabel?.wrapStyleWord = true

        regexPatternField?.document?.addDocumentListener(SimpleDocumentListener { updatePreview() })
        messageTemplateField?.document?.addDocumentListener(SimpleDocumentListener { updatePreview() })


        val previewPanel = JBScrollPane(previewLabel)

        val messageTemplatePanel = JBScrollPane(messageTemplateField)

        return FormBuilder.createFormBuilder()
            .addComponent(Label("Regex:"))
            .addComponent(regexPatternField!!)
            .addSeparator()

                .addComponent(Label("Template:"))
            .addComponent(messageTemplatePanel)
            .addSeparator()

            .addComponent(Label("Preview:"))
            .addComponent(previewPanel)
            .addSeparator()
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    private fun updatePreview() {
        try {
            val pattern = Pattern.compile(regexPatternField?.text ?: "")

            // Try to get a branch name from any open project
            var branchName = getCurrentBranchName()
            if (branchName.isNullOrEmpty()) {
                branchName = "123-sample-branch" // Default example
            }

            val matcher = pattern.matcher(branchName)
            if (matcher.find()) {
                var result = messageTemplateField?.text ?: ""
                for (i in 1..matcher.groupCount()) {
                    result = result.replace("$$i", matcher.group(i))
                }
                previewLabel?.text = "Sample message\n\n$result"
            } else {
                previewLabel?.text = "No match found in branch name: $branchName"
            }
        } catch (ex: PatternSyntaxException) {
            previewLabel?.text = "Invalid regex pattern"
        }
    }

    private fun getCurrentBranchName(): String? {
        ProjectManager.getInstance().openProjects.forEach { project ->
            GitUtil.getRepositories(project).forEach { repository ->
                repository.currentBranch?.let {
                    return it.name
                }
            }
        }
        return null
    }

    override fun isModified(): Boolean {
        val settings = CommitTemplateSettings.getInstance()
        return regexPatternField?.text != settings.regexPattern ||
                messageTemplateField?.text != settings.messageTemplate
    }

    override fun apply() {
        try {
            // Validate regex pattern
            Pattern.compile(regexPatternField?.text ?: "")

            val settings = CommitTemplateSettings.getInstance()
            settings.regexPattern = regexPatternField?.text ?: ""
            settings.messageTemplate = messageTemplateField?.text ?: ""
        } catch (ex: PatternSyntaxException) {
            Messages.showErrorDialog("Invalid regex pattern: ${ex.message}", "Invalid Configuration")
        }
    }

    override fun reset() {
        val settings = CommitTemplateSettings.getInstance()
        regexPatternField?.text = settings.regexPattern
        messageTemplateField?.text = settings.messageTemplate
        updatePreview()
    }
}

/**
 * A simple document listener that executes the provided action when the document is changed.
 */
private class SimpleDocumentListener(val action: () -> Unit) : javax.swing.event.DocumentListener {
    override fun insertUpdate(e: javax.swing.event.DocumentEvent) = action()
    override fun removeUpdate(e: javax.swing.event.DocumentEvent) = action()
    override fun changedUpdate(e: javax.swing.event.DocumentEvent) = action()
}