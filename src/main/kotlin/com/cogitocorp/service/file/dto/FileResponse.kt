package com.cogitocorp.service.file.dto

import java.time.Instant
import java.util.UUID

data class FileResponse(val name: String, val size: Long, val timestamp: Instant, val id: UUID) {
  constructor(file: File) : this(file.name, file.size, file.timestamp, file.id)
}
