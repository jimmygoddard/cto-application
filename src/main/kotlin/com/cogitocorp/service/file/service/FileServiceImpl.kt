package com.cogitocorp.service.file.service

import com.cogitocorp.service.file.dao.FileDao
import com.cogitocorp.service.file.dao.MostRecentFilesDao
import com.cogitocorp.service.file.dto.File
import com.cogitocorp.service.file.dto.ViewedTime
import org.springframework.stereotype.Service
import java.util.UUID;

@Service
class FileServiceImpl(
  private val fileDao: FileDao,
  private val mostRecentFilesDao: MostRecentFilesDao,
) : FileService {
  override fun saveFile(file: File): File {
    return fileDao.saveFile(file)
  }

  override fun getFiles(): List<File> {
    return fileDao.getFiles()
  }

  override fun getFile(id: UUID): File {
    val file = fileDao.getFile(id)
    mostRecentFilesDao.addViewedTime(ViewedTime(file.id))
    return file
  }
}
