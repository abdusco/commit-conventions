package dev.abdus.commitconventions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "CommitTemplateSettings",
    storages = [Storage("CommitTemplateSettings.xml")]
)
class CommitTemplateSettings : PersistentStateComponent<CommitTemplateSettings> {
    var regexPattern: String = "^(\\d+).*" // Default regex pattern
    var messageTemplate: String = "Closes: #\$1" // Default message template

    override fun getState(): CommitTemplateSettings = this

    override fun loadState(state: CommitTemplateSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): CommitTemplateSettings =
            ApplicationManager.getApplication().getService(CommitTemplateSettings::class.java)
    }
}

