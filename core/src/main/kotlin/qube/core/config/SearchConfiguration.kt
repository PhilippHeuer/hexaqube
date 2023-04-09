package qube.core.config

import io.quarkus.hibernate.search.orm.elasticsearch.SearchExtension
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer

@SearchExtension
class SearchConfiguration : ElasticsearchAnalysisConfigurer {
    override fun configure(context: ElasticsearchAnalysisConfigurationContext) {
        // filters
        context.tokenFilter( "snowball_english" )
            .type( "snowball" )
            .param( "language", "English" )

        // tokenizers
        context.tokenizer("pattern_dot")
            .type( "pattern" )
            .param( "pattern", "[.#()]" )

        // analyzers
        context.analyzer("text").custom()
            .tokenizer("standard")
            .tokenFilters("lowercase", "snowball_english", "asciifolding")
        context.analyzer("pattern_selector").custom()
            .tokenizer("pattern_dot")
            .tokenFilters("lowercase", "asciifolding")
    }
}
