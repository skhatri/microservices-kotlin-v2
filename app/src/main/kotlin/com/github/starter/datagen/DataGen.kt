package com.github.starter.datagen

import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*


class DataGen {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val cfg = Configuration(Configuration.VERSION_2_3_34)
            cfg.setDirectoryForTemplateLoading(File("./templates"))
            cfg.setDefaultEncoding("UTF-8")
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
            cfg.setLogTemplateExceptions(false)
            cfg.setWrapUncheckedExceptions(true)
            cfg.fallbackOnNullLoopVariable = false
            cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault())

            val rootData = Yaml().loadAs<Map<String, Any>>(FileInputStream("app/src/main/resources/templates/pokemon.yaml"), Map::class.java)

            val temp = cfg.getTemplate("repository.ftlh")
            val out = OutputStreamWriter(FileOutputStream(rootData["output"] as String))
            temp.process(rootData, out)
        }
    }
}