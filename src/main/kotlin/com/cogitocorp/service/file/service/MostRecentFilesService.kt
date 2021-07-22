package com.cogitocorp.service.file.service

import com.cogitocorp.service.file.dto.File

interface MostRecentFilesService {
  fun getMostRecentFiles(numFiles: Int): List<File>
  fun addViewedFile(file: File)
}
