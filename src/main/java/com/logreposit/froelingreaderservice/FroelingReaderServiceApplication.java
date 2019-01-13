package com.logreposit.froelingreaderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FroelingReaderServiceApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(FroelingReaderServiceApplication.class, args);
	}
}
