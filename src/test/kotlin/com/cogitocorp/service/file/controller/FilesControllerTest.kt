package com.cogitocorp.service.file.controller

import com.cogitocorp.service.file.dao.repository.FileRepository
import com.cogitocorp.service.file.dao.repository.ViewedTimeRepository
import com.cogitocorp.service.file.dao.repository.entity.FileEntity
import com.cogitocorp.service.file.dao.repository.entity.ViewedTimeEntity
import com.cogitocorp.service.file.dto.File
import com.cogitocorp.service.file.dto.FileResponse
import com.cogitocorp.service.file.dto.ViewedTime
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant
import java.util.zip.ZipInputStream
import kotlin.random.Random.Default.nextLong as randomLong


@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
class FilesControllerTest(
  @Autowired val mvc: MockMvc,
  @Autowired val viewedTimeRepository: ViewedTimeRepository,
  @Autowired val fileRepository: FileRepository,
  @Autowired val objectMapper: ObjectMapper,
) {

  companion object {
    @Container
    private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest")

    @DynamicPropertySource
    @JvmStatic
    fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
      registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
      registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
      registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
    }
  }

  @AfterEach
  internal fun tearDown() {
    fileRepository.deleteAll()
    viewedTimeRepository.deleteAll()
  }

  @Test
  fun createFile() {
    val multipartFile = MockMultipartFile("file", "test.txt",
      "text/plain", "file contents".toByteArray())
    val result = mvc.perform(multipart("/files").file(multipartFile))
      .andExpect(status().isOk)
      .andReturn()
    val fileResponse = objectMapper.readValue<FileResponse>(result.response.contentAsString)
    val actualViewedTime = viewedTimeRepository.findByOrderByViewedTimeDesc(Pageable.ofSize(1))
    Assertions.assertEquals(emptyList<ViewedTime>(), actualViewedTime)
    val maybeFile = fileRepository.findById(fileResponse.id)
    Assertions.assertTrue(maybeFile.isPresent)
    val actualFile = maybeFile.get()
    println(actualFile)
    Assertions.assertEquals(actualFile.id, fileResponse.id)
  }

  @Test
  fun getFiles() {
    val files = (0..4)
      .map { File("file-$it", randomLong(), Instant.now(), "contents") }
      .map { fileRepository.save(FileEntity(it)) }
    val results = mvc.perform(get("/files")).andExpect(status().isOk).andReturn()
    val actualFileResponses =
      objectMapper.readValue<List<FileResponse>>(results.response.contentAsString)
    Assertions.assertEquals(
      files.map { it.id }.sorted(),
      actualFileResponses.map { it.id }.sorted())

  }

  @Test
  fun `Will return the expected file if it exists`() {
    val entity = fileRepository.save(FileEntity("name", randomLong(), Instant.now(), "contents"))
    val result = mvc.perform(get("/files/${entity.id}"))
      .andExpect(status().isOk)
      .andReturn()
    val fileResponse = objectMapper.readValue<FileResponse>(result.response.contentAsString)
    Assertions.assertEquals(entity.id, fileResponse.id)
    Assertions.assertEquals(entity.name, fileResponse.name)
    Assertions.assertEquals(entity.size, fileResponse.size)
  }

  @Test
  fun zipFile() {
    val entity =
      fileRepository.save(FileEntity("name", randomLong(0, 123456), Instant.now(), "contents"))
    val result = mvc.perform(get("/files/${entity.id}/zip"))
      .andExpect(status().isOk)
      .andReturn()
    val zipInputStream = ZipInputStream(result.response.contentAsByteArray.inputStream())
    Assertions.assertEquals(entity.id.toString(), zipInputStream.nextEntry?.name)

  }

  @Test
  fun zipFiles() {
    val files = (0..4)
      .map { File("file-$it", randomLong(0, 123456), Instant.now(), "contents") }
      .map { fileRepository.save(FileEntity(it)) }
    val result = mvc.perform(get("/files/zip"))
      .andExpect(status().isOk)
      .andReturn()
    val zipInputStream = ZipInputStream(result.response.contentAsByteArray.inputStream())
    val actualFileNames = mutableListOf<String>()
    var nextEntry = zipInputStream.nextEntry
    while (nextEntry != null) {
      actualFileNames.add(nextEntry.name)
      nextEntry = zipInputStream.nextEntry
    }
    Assertions.assertEquals(files.map { it.id.toString() }.sorted(), actualFileNames.sorted())
  }

  @Test
  fun getMostRecentFiles() {
    val files = (0..4)
      .map { File(it.toString(), randomLong(0, 123456), Instant.now(), "contents") }
      .map { fileRepository.save(FileEntity(it)) }
    files
      .filter { it.name.toInt() % 2 == 0 }
      .forEach { viewedTimeRepository.save(ViewedTimeEntity(it.id, Instant.now())) }
    val results = mvc.perform(get("/files/most-recent"))
      .andExpect { status().isOk }
      .andReturn()
    val actualFiles = objectMapper.readValue<List<FileResponse>>(results.response.contentAsString)
    Assertions.assertEquals(
      files.filter { it.name.toInt() % 2 == 0 }.map { it.id.toString() }.sorted(),
      actualFiles.map { it.id.toString() }.sorted())
  }

  @Test
  fun zipMostRecentFiles() {
    val files = (0..4)
      .map { File(it.toString(), randomLong(0, 123456), Instant.now(), "contents") }
      .map { fileRepository.save(FileEntity(it)) }
    files
      .filter { it.name.toInt() % 2 == 0 }
      .forEach { viewedTimeRepository.save(ViewedTimeEntity(it.id, Instant.now())) }
    val result = mvc.perform(get("/files/most-recent/zip"))
      .andExpect(status().isOk)
      .andReturn()
    val zipInputStream = ZipInputStream(result.response.contentAsByteArray.inputStream())
    val actualFileNames = mutableListOf<String>()
    var nextEntry = zipInputStream.nextEntry
    while (nextEntry != null) {
      actualFileNames.add(nextEntry.name)
      nextEntry = zipInputStream.nextEntry
    }
    Assertions.assertEquals(
      files.filter { it.name.toInt() % 2 == 0 }.map { it.id.toString() }.sorted(),
      actualFileNames.sorted())
  }
}
