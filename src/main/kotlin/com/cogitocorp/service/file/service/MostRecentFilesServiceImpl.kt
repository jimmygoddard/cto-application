package com.cogitocorp.service.file.service

import com.cogitocorp.service.file.dao.MostRecentFilesDao
import com.cogitocorp.service.file.dto.ViewedTime
import org.springframework.stereotype.Service

@Service
class MostRecentFilesServiceImpl(
  private val mostRecentFilesDao: MostRecentFilesDao,
) : MostRecentFilesService {
  override fun getMostRecentFiles(numFiles: Int): List<ViewedTime> {
    return mostRecentFilesDao.getMostRecentFiles(numFiles)
  }

  override fun addViewedFile(viewedTime: ViewedTime): ViewedTime {
    return mostRecentFilesDao.addViewedFile(viewedTime)
  }
}
