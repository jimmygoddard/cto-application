package com.cogitocorp.service.file.service

import com.cogitocorp.service.file.dao.FileDao
import com.cogitocorp.service.file.dto.File
import org.springframework.stereotype.Service
import java.util.UUID;

@Service
class FileServiceImpl(private val fileDao: FileDao) : FileService {
  override fun saveFile(file: File): File {
    return fileDao.saveFile(file)
  }

  override fun getFiles(): List<File> {
    return fileDao.getFiles()
  }

  override fun getFile(id: UUID): File {
    return fileDao.getFile(id)
  }
}
