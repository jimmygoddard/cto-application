package com.cogitocorp.service.file.dao

import com.cogitocorp.service.file.dao.repository.entity.ViewedTimeEntity
import com.cogitocorp.service.file.dao.repository.entity.ViewedTimeRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class MostRecentFilesDaoImpl(
  private val viewedTimeRepository: ViewedTimeRepository,
) : MostRecentFilesDao {

  override fun getMostRecentFiles(numFiles: Int): List<UUID> {
    val viewedTimes = viewedTimeRepository.findByOrderByViewedTimeDesc(Pageable.ofSize(numFiles))
    return viewedTimes.map { it.fileId }
  }

  override fun addViewedFile(fileId: UUID) {
    viewedTimeRepository.save(ViewedTimeEntity(fileId, Instant.now()))
  }
}
