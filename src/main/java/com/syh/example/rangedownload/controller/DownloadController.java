package com.syh.example.rangedownload.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.syh.example.rangedownload.MultipartFileSender;
import com.syh.example.rangedownload.resource.LocalRangedResource;
import com.syh.example.rangedownload.stream.metrics.MetricsManager;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author shen.yuhang
 * created on 2020/10/12
 **/

@RestController
@Slf4j
public class DownloadController {

	@Value("${workspace.dir}")
	private String workspaceDir;

	@GetMapping("/{fileName}")
	public void download(
		@PathVariable("fileName") String fileName,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException {

		MultipartFileSender.create(new LocalRangedResource(workspaceDir + "/" + fileName))
			.with(request)
			.with(response)
			.done();
	}

	@Scheduled(fixedDelay = 5000)
	public void showMetrics() {
		log.info("[Metrics] total download size: {}", MetricsManager.print());
	}
}
