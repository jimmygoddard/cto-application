package com.cogitocorp.service.file.service

import com.cogitocorp.service.file.dto.File
import java.util.UUID

interface FileService {
  fun saveFile(file: File): File
  fun getFile(id: UUID): File
  fun getFiles(): List<File>
}
