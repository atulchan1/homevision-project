
package com.homevision.homecontent;


import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.CommandLineRunner;

import com.homevision.homecontent.service.HouseContentService;

public class HomeContentDownloadJobTest {
	private HomeContentDowloadJob job = new HomeContentDowloadJob();

	@Test
	public void testCommandLineRunnerWithDefaultPages() throws Exception {
       HouseContentService service = Mockito.mock(HouseContentService.class);
	   CommandLineRunner runner = job.run(service);
	   doNothing().when(service).retrieveContent(1, 10, 25, 50);
	   runner.run(new String[]{});
	   Mockito.verify(service).retrieveContent(1, 10, 25, 50);
    }

	@Test
	public void testCommandLineRunnerWithCustomPages() throws Exception {
       HouseContentService service = Mockito.mock(HouseContentService.class);
	   CommandLineRunner runner = job.run(service);
	   doNothing().when(service).retrieveContent(1, 2, 25, 50);
	   runner.run(new String[]{"1","2"});
	   Mockito.verify(service).retrieveContent(1, 2, 25, 50);
    }
	@Test
	public void testCommandLineRunnerWithInvalidArgs() throws Exception {
       HouseContentService service = Mockito.mock(HouseContentService.class);
	   CommandLineRunner runner = job.run(service);
	   doNothing().when(service).retrieveContent(1, 10, 25, 50);
	   runner.run(new String[]{"10","2"});
	   Mockito.verify(service).retrieveContent(1, 10, 25, 50);
    }
	@Test
	public void testCommandLineRunnerWithInvalidNonNumberArgs() throws Exception {
       HouseContentService service = Mockito.mock(HouseContentService.class);
	   CommandLineRunner runner = job.run(service);
	   doNothing().when(service).retrieveContent(1, 10, 25, 50);
	   runner.run(new String[]{"Testing","2"});
	   Mockito.verify(service).retrieveContent(1, 10, 25, 50);
    }
}
