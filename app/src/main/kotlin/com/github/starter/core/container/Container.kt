package com.github.starter.core.container;

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class Container<T> @JsonCreator constructor(
    @JsonProperty("data")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    val data: T?, @JsonProperty("metadata") val metadata: Map<String, Any>,
    @JsonProperty("errors") val errors: List<MessageItem>, @JsonProperty("warnings") val warnings: List<MessageItem>
) {
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    constructor(data: T) : this(data, mapOf(), listOf(), listOf())

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    constructor(errors: List<MessageItem>) : this(null, mapOf(), errors, listOf());
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    constructor(data: T, metadata: Map<String, Any>) : this(data, metadata, listOf(), listOf())
}
