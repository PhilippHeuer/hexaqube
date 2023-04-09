package qube.core.template

import com.github.jknack.handlebars.Handlebars
import io.quarkus.cache.CacheResult
import jakarta.enterprise.context.ApplicationScoped
import qube.core.event.domain.QubeInstance
import qube.core.template.domain.TemplateRenderResult
import qube.core.template.jpa.TemplateEntity

@ApplicationScoped
class TemplateEngine {
    private val handlebars = Handlebars()

    fun render(templateId: String, instance: QubeInstance, data: Map<String, Any>): TemplateRenderResult {
        val template = getTemplate(templateId, instance)

        return TemplateRenderResult(
            title = template.title?.let { processTemplate(it, data) },
            content = template.content?.let { processTemplate(it, data) } ?: "",
            footer = template.footer?.let { processTemplate(it, data) },
        )
    }

    @CacheResult(cacheName = "template-cache")
    fun getTemplate(templateId: String, instance: QubeInstance): TemplateEntity {
        return TemplateEntity.findByTemplateIdAndInstance(templateId, instance).firstOrNull() ?: TemplateEntity.findByTemplateId(templateId).first()
    }

    private fun processTemplate(template: String, data: Map<String, Any>): String {
        val tpl = handlebars.compileInline(template)
        return tpl.apply(data)
    }
}
