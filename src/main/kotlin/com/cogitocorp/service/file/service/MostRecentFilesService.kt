package com.cogitocorp.service.file.service

import com.cogitocorp.service.file.dto.ViewedTime

interface MostRecentFilesService {
  fun getMostRecentFiles(numFiles: Int): List<ViewedTime>
  fun addViewedFile(viewedTime: ViewedTime): ViewedTime
}
