package com.homevision.homecontent.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.ExecutorService;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import com.homevision.homecontent.model.House;
import com.homevision.homecontent.model.HouseResponse;
import com.homevision.homecontent.repository.FileRepository;

@Service
public class HouseContentService {
  private static final Logger log = LoggerFactory.getLogger(HouseContentService.class);
  private final ExecutorService pool;

  private RestTemplate restTemplate;
  private FileRepository repository;
  private String serviceURL;

  @Autowired
  HouseContentService(RestTemplate restTemplate, FileRepository repository, ExecutorService pool,
      @Value("${service.url}") String serviceURL) {
    this.restTemplate = restTemplate;
    this.repository = repository;
    this.serviceURL = serviceURL;
    this.pool = pool;
  }

  public void retrieveContent(int startingPage, int endingPage, int retryDelayLimit, int retryLimit) throws Exception {
    House[] houses = null;
    for (int i = startingPage; i <= endingPage; i++) {
      // TODO Rather than brute force retries use spring-retry
      int limit = 0;
      while ((houses = retrievePageContent(i)) == null) {
        if (++limit > retryDelayLimit) {
          Thread.sleep(1000);
        }
        if (limit > retryLimit) {
          throw new IllegalStateException("Service unavailable at this time");
        }
      }
      persistDetails(houses);
    }
    pool.shutdown();
    // TODO move all hardcoded values to config
    pool.awaitTermination(5, TimeUnit.MINUTES);
    System.exit(0);
  }

  protected House[] retrievePageContent(int pageNumber) throws Exception {
    House[] houses = null;
    try {
      // TODO Remove unnecessary logging.
      log.info(serviceURL);
      ResponseEntity<HouseResponse> response = restTemplate.getForEntity(serviceURL + pageNumber, HouseResponse.class);
      houses = response.getBody().houses();
      for (House house : houses) {
        log.info(house.toString());
      }
    } catch (Exception e) {
      return houses;
    }
    return houses;
  }

  protected void persistDetails(House[] houses) throws Exception {
    for (House house : houses) {

      pool.submit(() -> downloadImage(house));

    }
  }

  protected void downloadImage(House house) {
    String fileName = constructFileName(house);
    try{
      InputStream in = getInputStream(house.photoURL()); 
      log.info("URL = " + house.photoURL() + " Filename =" + fileName);
      repository.persist(in, fileName);

    } catch (Exception e) {
      log.error("Unable to download image " + fileName, e);
    }
  }

  protected String constructFileName(House house) {
    return house.id() + "_" + cleanString(house.address())
        + house.photoURL().substring(house.photoURL().lastIndexOf("."));
  }

  protected String cleanString(String address) {
    return address.replaceAll("/", "_");
  }

  protected InputStream getInputStream(String url) throws Exception {
    return new URL(url).openStream();
  }
  // TODO Send report emails with details of homes fetched and image persisted.
  // Include failed image persistence details.
  // TODO Add monitoring for successful fetches from rest service vs failures and
  // also metrics on files persisted, service latency etc.

}