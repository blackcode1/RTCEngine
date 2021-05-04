package com.example.RTCEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com")
@EnableAsync
public class RtcEngineApplication {

	public static void main(String[] args) {
		RtcEngineMemory.updateRtcEngineMemoryFromXML();
		SpringApplication.run(RtcEngineApplication.class, args);
		RtcEngineMemory.updateRtcEngineMemoryFromDB();
	}

}
