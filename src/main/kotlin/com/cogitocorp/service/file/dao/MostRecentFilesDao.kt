package com.cogitocorp.service.file.dao

import java.util.UUID

interface MostRecentFilesDao {
  fun getMostRecentFiles(numFiles: Int): List<UUID>
  fun addViewedFile(fileId: UUID)
}
