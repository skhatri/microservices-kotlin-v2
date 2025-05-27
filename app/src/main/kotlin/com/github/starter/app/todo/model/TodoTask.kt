package com.github.starter.app.todo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

class
TodoTask @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("action_by") val actionBy: String?,
    @JsonProperty("created") val created: LocalDateTime?,
    @JsonProperty("status") val status: String?,
    @JsonProperty("updated") val updated: LocalDateTime?
) : Serializable {
    override fun toString(): String {
        return StringBuilder("id:").append(id).append(", description: ").append(description).append(", action_by: ")
            .append(actionBy)
            .append(", created: ").append(created).append(", status: ").append(status).append(", updated: ")
            .append(updated).toString();
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TodoTask) return false
        return id == other.id &&
                description == other.description &&
                actionBy == other.actionBy &&
                created == other.created &&
                status == other.status &&
                updated == other.updated
    }

    override fun hashCode(): Int {
        return Objects.hash(id, description, actionBy, created, status, updated)
    }
}
