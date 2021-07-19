package com.cogitocorp.service.file.controller

import mu.KotlinLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes

private val logger = KotlinLogging.logger {}

@Controller
class FileUploadController {
  private val storage = mutableMapOf<String, Any>();

  @PostMapping("/")
  fun handleFileUpload(@RequestParam("file") file: MultipartFile, redirectAttributes: RedirectAttributes): String? {
    logger.info("Storing new file", file);
    storage.put(file.name, file);
    return file.name;
  }}
