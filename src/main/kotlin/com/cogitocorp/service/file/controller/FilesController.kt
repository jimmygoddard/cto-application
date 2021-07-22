package com.cogitocorp.service.file.controller

import com.cogitocorp.service.file.ServerConfig
import com.cogitocorp.service.file.dto.File
import com.cogitocorp.service.file.dto.FileResponse
import com.cogitocorp.service.file.dto.ViewedTime
import com.cogitocorp.service.file.service.FileService
import com.cogitocorp.service.file.service.MostRecentFilesService
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
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.servlet.http.HttpServletResponse


private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/files")
class FilesController(
  private val fileService: FileService,
  private val mostRecentFilesService: MostRecentFilesService,
  private val serverConfig: ServerConfig,
) {

  @PostMapping("/")
  fun createFile(@RequestParam file: MultipartFile): FileResponse {
    logger.info("Storing new file. file=$file")
    val fileToSave = File(file.name, file.size, Instant.now(), String(file.bytes))
    val response = fileService.saveFile(fileToSave)
    return FileResponse(response)
  }

  @GetMapping("/")
  fun getFiles(): List<FileResponse> {
    logger.info("Getting files")
    return fileService.getFiles().map { FileResponse(it) }
  }

  @GetMapping("/{id}")
  fun getFile(@PathVariable id: UUID): FileResponse {
    logger.info("Getting file. id=$id")
    val file = fileService.getFile(id)
    mostRecentFilesService.addViewedFile(ViewedTime(file.id))
    return FileResponse(file)
  }

  @GetMapping(value = ["/{id}/zip"], produces = ["application/zip"])
  fun zipFile(@PathVariable id: UUID, response: HttpServletResponse) {
    logger.info("Zipping file. id=$id")
    //setting headers
    response.status = HttpServletResponse.SC_OK
    val fileName = "zipped-file-$id.zip"
    response.addHeader("Content-Disposition", "attachment; filename=$fileName")
    val zipOutputStream = ZipOutputStream(response.outputStream)
    val file = fileService.getFile(id)
    mostRecentFilesService.addViewedFile(ViewedTime(file.id))
    zipOutputStream.putNextEntry(ZipEntry(file.id.toString()))
    val fileInputStream = file.contents.byteInputStream()
    fileInputStream.copyTo(zipOutputStream)
    fileInputStream.close()
    zipOutputStream.closeEntry()
    zipOutputStream.close()
  }

  // from https://stackoverflow.com/a/40498539
  @GetMapping(value = ["/zip"], produces = ["application/zip"])
  fun zipFiles(response: HttpServletResponse) {
    logger.info("Zipping all files")
    //setting headers
    response.status = HttpServletResponse.SC_OK
    val fileName = "zipped-files-${Instant.now().epochSecond}.zip"
    response.addHeader("Content-Disposition", "attachment; filename=$fileName")
    val zipOutputStream = ZipOutputStream(response.outputStream)

    // create a list to add files to be zipped
    val files = fileService.getFiles()

    // package files
    files.forEach {
      zipOutputStream.putNextEntry(ZipEntry(it.id.toString()))
      val fileInputStream = it.contents.byteInputStream()
      fileInputStream.copyTo(zipOutputStream)
      fileInputStream.close()
      zipOutputStream.closeEntry()
    }
    zipOutputStream.close()
  }

  @GetMapping("/most-recent")
  fun getMostRecentFiles(): List<FileResponse> {
    val numFiles = serverConfig.mostRecentFilesCount
    logger.info("Getting most recent files. numFiles=$numFiles")
    return mostRecentFilesService.getMostRecentFiles(numFiles).map {
      FileResponse(fileService.getFile(it.fileId))
    }
  }

  @GetMapping(value = ["/most-recent/zip"], produces = ["application/zip"])
  fun zipMostRecentFiles(response: HttpServletResponse) {
    val numFiles = serverConfig.mostRecentFilesCount
    logger.info("Zipping most recent files. numFiles=$numFiles")
    //setting headers
    response.status = HttpServletResponse.SC_OK
    val fileName = "zipped-most-recent-files-${Instant.now().epochSecond}.zip"
    response.addHeader("Content-Disposition", "attachment; filename=$fileName")
    val zipOutputStream = ZipOutputStream(response.outputStream)

    // create a list to add files to be zipped
    val files = mostRecentFilesService.getMostRecentFiles(numFiles).map {
      fileService.getFile(it.fileId)
    }

    // package files
    files.forEach {
      zipOutputStream.putNextEntry(ZipEntry(it.id.toString()))
      val fileInputStream = it.contents.byteInputStream()
      fileInputStream.copyTo(zipOutputStream)
      fileInputStream.close()
      zipOutputStream.closeEntry()
    }
    zipOutputStream.close()
  }
}
