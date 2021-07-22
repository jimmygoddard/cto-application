package com.cogitocorp.service.file.dao

import com.cogitocorp.service.file.dao.repository.entity.ViewedTimeEntity
import com.cogitocorp.service.file.dao.repository.entity.ViewedTimeRepository
import com.cogitocorp.service.file.dto.ViewedTime
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class MostRecentFilesDaoImpl(
  private val viewedTimeRepository: ViewedTimeRepository,
) : MostRecentFilesDao {

  override fun getMostRecentFiles(numFiles: Int): List<ViewedTime> {
    val viewedTimes = viewedTimeRepository.findByOrderByViewedTimeDesc(Pageable.ofSize(numFiles))
    return viewedTimes.map { ViewedTime(it) }
  }

  override fun addViewedFile(viewedTime: ViewedTime): ViewedTime {
    return ViewedTime(viewedTimeRepository.save(ViewedTimeEntity(viewedTime)))
  }
}
