package com.cogitocorp.service.file.dto

import java.time.Instant
import java.util.UUID

data class ViewedTime(val fileId: UUID, val viewedTime: Instant)
