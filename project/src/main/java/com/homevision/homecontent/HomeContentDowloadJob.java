package com.homevision.homecontent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import com.homevision.homecontent.service.HouseContentService;

@SpringBootApplication
@Configuration
@PropertySource("classpath:application.properties")
public class HomeContentDowloadJob {

	private static final Logger log = LoggerFactory.getLogger(HomeContentDowloadJob.class);

	public static void main(String[] args) {
		new SpringApplicationBuilder(HomeContentDowloadJob.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	@Bean
	public ExecutorService pool() {
		return Executors.newFixedThreadPool(10);
	}

	@Bean
	public CommandLineRunner run(HouseContentService houseContentService) throws Exception {
		// TODO Move all hardcoded values to config. And have different config per
		// profile (dev/prod/qa).
		return args -> {
			int startingPage = 1;
			int endingPage = 10;
			if (args.length > 1) {
				try {
					int start = Integer.parseInt(args[0]);
					int end = Integer.parseInt(args[1]);
					log.info("Started "+ start+ " "+ end);
					if (start <= end) {
						startingPage = start;
						endingPage = end;

					} else {
						log.error(
								"Start page argument should be less than end page from command line, reverting to default 1 to 10 pages");

					}

				} catch (Exception e) {
					log.error(
							"Unable to read start and end pages from command line, reverting to  default 1 to 10 pages");
				}
			}
			houseContentService.retrieveContent(startingPage, endingPage, 25, 50);
		};
}
}
