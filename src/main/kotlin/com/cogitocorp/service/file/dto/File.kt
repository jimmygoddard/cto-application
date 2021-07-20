package com.cogitocorp.service.file.dto

import com.cogitocorp.service.file.dao.repository.entity.FileEntity
import java.time.Instant
import java.util.UUID;

data class File(
  val name: String,
  val size: Long,
  val timestamp: Instant,
  val contents: String,
  val id: UUID = UUID.randomUUID(),
) {
  constructor(fileEntity: FileEntity) : this(
    fileEntity.name,
    fileEntity.size,
    fileEntity.timestamp,
    fileEntity.contents,
    fileEntity.id,
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is File) return false

    if (id != other.id) return false
    if (name != other.name) return false
    if (size != other.size) return false
    if (timestamp != other.timestamp) return false
    if (!contents.contentEquals(other.contents)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + size.hashCode()
    result = 31 * result + timestamp.hashCode()
    result = 31 * result + contents.hashCode()
    return result
  }

}
