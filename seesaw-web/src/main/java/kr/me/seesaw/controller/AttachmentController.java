package kr.me.seesaw.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.me.seesaw.core.file.FileIOService;
import kr.me.seesaw.domain.Article;
import kr.me.seesaw.domain.Attachment;
import kr.me.seesaw.service.ArticleService;
import kr.me.seesaw.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@Controller
@RequestMapping("/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    private final ArticleService articleService;

    @GetMapping("/{id}/download")
    public void getAttachment(
            @PathVariable("id") String id,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            HttpServletResponse response
    ) throws IOException {
        Attachment attachment = attachmentService.find(id);
        String fileName = URLEncoder.encode(attachment.getOriginalName(), StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = FileIOService.read(attachmentService.getAbsolutePath(id));

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition(userAgent, fileName));
        OutputStream outputStream = response.getOutputStream();
        FileCopyUtils.copy(inputStream, outputStream);
    }

    @GetMapping("/download-zip")
    public void getAttachments(
            @RequestParam String articleId,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            HttpServletResponse response
    ) throws IOException {
        Article article = articleService.find(articleId);
        List<Attachment> attachments = article.getAttachments();
        if (attachments.isEmpty()) {
            throw new NoSuchElementException();
        }
        String fileName = URLEncoder.encode(article.getTitle(), StandardCharsets.UTF_8);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition(userAgent, fileName) + ".zip");

        try (ZipOutputStream outputStream = new ZipOutputStream(response.getOutputStream())) {
            for (Attachment attachment : attachments) {
                ZipEntry entry = new ZipEntry(attachment.getOriginalName());
                outputStream.putNextEntry(entry);
                ByteArrayInputStream inputStream = FileIOService.read(attachmentService.getAbsolutePath(attachment));
                StreamUtils.copy(inputStream, outputStream);
                outputStream.closeEntry();
            }
        }
    }

    private String getContentDisposition(String userAgent, String fileName) {
        String disposition = "attachment;filename=";
        if (userAgent.contains("MSIE")) {
            int i = userAgent.indexOf('M', 2);
            String IEV = userAgent.substring(i + 5, i + 8);
            disposition = IEV.equalsIgnoreCase("5.5") ? "filename=" : disposition;
        }
        return disposition + fileName;
    }
}
