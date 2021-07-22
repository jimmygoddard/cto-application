package com.cogitocorp.service.file.dto

import com.cogitocorp.service.file.dao.repository.entity.ViewedTimeEntity
import java.time.Instant
import java.util.UUID

data class ViewedTime(val fileId: UUID, val viewedTime: Instant = Instant.now()) {
  constructor(viewedTimeEntity: ViewedTimeEntity) :
      this(viewedTimeEntity.fileId, viewedTimeEntity.viewedTime)
}
