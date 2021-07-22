package com.cogitocorp.service.file.controller

import com.cogitocorp.service.file.dao.repository.FileRepository
import com.cogitocorp.service.file.dao.repository.ViewedTimeRepository
import com.cogitocorp.service.file.dto.FileResponse
import com.cogitocorp.service.file.dto.ViewedTime
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


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
  }

  @Test
  fun getFile() {
  }

  @Test
  fun zipFile() {
  }

  @Test
  fun zipFiles() {
  }

  @Test
  fun getMostRecentFiles() {
  }

  @Test
  fun zipMostRecentFiles() {
  }
}
