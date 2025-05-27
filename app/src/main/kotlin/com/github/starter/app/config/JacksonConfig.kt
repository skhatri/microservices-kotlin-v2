package com.github.starter.app.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.github.starter.core.container.Container
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
open class JacksonConfig {
    @Bean
    open fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(SimpleModule().addSerializer(Container::class.java, ContainerSerializer()))
            val timeModule = JavaTimeModule()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            timeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(formatter))
            timeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(formatter))
            registerModules(timeModule, Jdk8Module())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
}

class ContainerSerializer : JsonSerializer<Container<*>>() {
    override fun serialize(value: Container<*>, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeObjectField("meta", value.metadata)
        gen.writeObjectField("data", value.data)
        gen.writeObjectField("errors", value.errors)
        gen.writeObjectField("warnings", value.warnings)
        gen.writeEndObject()
    }
}
