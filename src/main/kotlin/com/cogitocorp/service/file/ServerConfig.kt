package com.cogitocorp.service.file

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "file-service")
data class ServerConfig(
  val mostRecentFilesCount: Int = 10,
)

