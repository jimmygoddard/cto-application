package com.cogitocorp.service.file.dao.repository

import com.cogitocorp.service.file.dao.repository.entity.FileEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface FileRepository: JpaRepository<FileEntity, UUID> {
  override fun findAll(): List<FileEntity>
  override fun findById(id: UUID): Optional<FileEntity>
}
