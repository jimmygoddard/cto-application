package com.cogitocorp.service.file.dao

import com.cogitocorp.service.file.dto.File
import java.util.UUID

interface FileDao {
  fun saveFile(file: File): File
  fun getFile(id: UUID): File
  fun getFiles(): List<File>
}
