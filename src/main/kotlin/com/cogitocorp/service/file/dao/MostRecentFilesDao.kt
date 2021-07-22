package com.cogitocorp.service.file.dao

import com.cogitocorp.service.file.dto.ViewedTime

interface MostRecentFilesDao {
  fun getMostRecentFiles(numFiles: Int): List<ViewedTime>
  fun addViewedTime(viewedTime: ViewedTime): ViewedTime
}
