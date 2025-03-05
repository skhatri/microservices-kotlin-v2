package ${packageName}

import com.github.pokemon.model.${objectName}
import com.github.starter.app.config.JdbcClientFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
open class ${objectName}Repository(
@Autowired clientFactory: JdbcClientFactory,
@Value("\${"$"}{flags.default-jdbc-client}") jdbcClientName: String) {
private val client: DatabaseClient = clientFactory.forName(jdbcClientName).client()

fun listEntries(start: Int = 0, limit: Int = 10): Flux<${objectName}> {
    return client.sql(
        """select <#if columns?? && columns?size gt 0>${columns?map(it -> it.name)?join(", ")}</#if>
        |from ${resourceName}<#if order?? && order?size gt 0>
        |ORDER BY ${order?join(", ")}</#if>
        |limit $limit
        |offset $start
        """.trimMargin())
    .mapProperties(${objectName}::class.java).all()
    }
}