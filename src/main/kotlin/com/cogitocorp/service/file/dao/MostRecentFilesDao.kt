package com.cogitocorp.service.file.dao

import com.cogitocorp.service.file.dto.ViewedTime

interface MostRecentFilesDao {
  fun getMostRecentFiles(numFiles: Int): List<ViewedTime>
  fun addViewedFile(viewedTime: ViewedTime): ViewedTime
}
