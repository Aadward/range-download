package com.syh.example.rangedownload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RangeDownloadApplication {

	public static void main(String[] args) {
		SpringApplication.run(RangeDownloadApplication.class, args);
	}

}
