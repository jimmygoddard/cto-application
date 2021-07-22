package com.cogitocorp.service.file.service

import com.cogitocorp.service.file.dao.FileDao
import com.cogitocorp.service.file.dao.MostRecentFilesDao
import com.cogitocorp.service.file.dto.File
import org.springframework.stereotype.Service

@Service
class MostRecentFilesServiceImpl(
  private val mostRecentFilesDao: MostRecentFilesDao,
  private val fileDao: FileDao,
) : MostRecentFilesService {
  override fun getMostRecentFiles(numFiles: Int): List<File> {
    return mostRecentFilesDao.getMostRecentFiles(numFiles).map { fileDao.getFile(it) }
  }

  override fun addViewedFile(file: File) {
    mostRecentFilesDao.addViewedFile(file.id)
  }
}
