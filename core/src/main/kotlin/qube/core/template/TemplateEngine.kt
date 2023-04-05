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

        val titleTemplate = handlebars.compileInline(template.title)
        val contentTemplate = handlebars.compileInline(template.content)
        val footerTemplate = handlebars.compileInline(template.footer)

        return TemplateRenderResult(
            title = titleTemplate.apply(data),
            content = contentTemplate.apply(data),
            footer = footerTemplate.apply(data),
        )
    }

    @CacheResult(cacheName = "template-cache")
    fun getTemplate(templateId: String, instance: QubeInstance): TemplateEntity {
        return TemplateEntity.findByTemplateIdAndInstance(templateId, instance).firstOrNull() ?: TemplateEntity.findByTemplateId(templateId).first()
    }
}
