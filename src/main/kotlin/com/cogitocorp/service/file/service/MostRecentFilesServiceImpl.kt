package com.cogitocorp.service.file.service

import com.cogitocorp.service.file.dao.FileDao
import com.cogitocorp.service.file.dao.MostRecentFilesDao
import com.cogitocorp.service.file.dto.File
import org.springframework.stereotype.Service

@Service
class MostRecentFilesServiceImpl(
  private val mostRecentFilesDao: MostRecentFilesDao,
  private val filesDao: FileDao,
) : MostRecentFilesService {
  override fun getMostRecentFiles(numFiles: Int): List<File> {
    val viewedTimes = mostRecentFilesDao.getMostRecentFiles(numFiles)
    return viewedTimes.map { filesDao.getFile(it.fileId) }
  }
}
