package com.cogitocorp.service.file.dao

import com.cogitocorp.service.file.dao.repository.FileRepository
import com.cogitocorp.service.file.dao.repository.entity.FileEntity
import com.cogitocorp.service.file.dto.File
import com.cogitocorp.service.file.exception.NotFoundException
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FileDaoImpl(private val fileRepository: FileRepository) : FileDao {

  override fun saveFile(file: File): File {
    val fileEntity = FileEntity(file)
    return File(fileRepository.save(fileEntity))
  }

  override fun getFile(id: UUID): File {
    val maybeFile = fileRepository.findById(id)
    if (maybeFile.isEmpty) {
      throw NotFoundException("File with id=${id} not found")
    }
    return File(maybeFile.get())
  }

  override fun getFiles(): List<File> {
    return fileRepository.findAll().map { f -> File(f) }
  }
}
