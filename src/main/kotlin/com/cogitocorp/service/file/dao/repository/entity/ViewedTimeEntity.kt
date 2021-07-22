package com.cogitocorp.service.file.dao.repository.entity

import java.time.Instant
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "file_viewed_time")
class ViewedTimeEntity(@Id val fileId: UUID, val viewedTime: Instant)

