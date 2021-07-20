package com.cogitocorp.service.file.controller

import com.cogitocorp.service.file.dto.File
import com.cogitocorp.service.file.dto.FileResponse
import com.cogitocorp.service.file.service.FileService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.UUID

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/files")
class FilesController(val fileService: FileService) {

  @PostMapping("/")
  fun createFile(@RequestParam file: MultipartFile): FileResponse {
    logger.info("Storing new file", file)
    val fileToSave = File(file.name, file.size, Instant.now(), String(file.bytes))
    val response = fileService.saveFile(fileToSave)
    return FileResponse(response)
  }

  @GetMapping("/")
  fun getFiles(): List<FileResponse> {
    logger.info("Getting files")
    return fileService.getFiles().map { f -> FileResponse(f) }
  }

  @GetMapping("/{id}")
  fun getFile(@PathVariable id: UUID): FileResponse {
    logger.info("Getting file for id=${id}")
    return FileResponse(fileService.getFile(id))
  }
}
