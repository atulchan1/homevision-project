package com.homevision.homecontent.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.homevision.homecontent.repository.FileRepository;
import com.homevision.homecontent.model.House;
import com.homevision.homecontent.model.HouseResponse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import java.util.concurrent.ExecutorService;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HouseContentServiceTest {
    private RestTemplate template = Mockito.mock(RestTemplate.class);
    private FileRepository repository = Mockito.mock(FileRepository.class);
    private ExecutorService pool = Mockito.mock(ExecutorService.class);
    private String serviceURL = "http://localhost:8080/test?page=";
    private HouseContentService service = new HouseContentService(template, repository, pool, serviceURL) {
        @Override
        protected InputStream getInputStream(String url) throws Exception {
            return null;
        }
    };
    private HouseContentService serviceFail = new HouseContentService(template, repository, pool, serviceURL) {
        @Override
        protected House[] retrievePageContent(int pageNumber) throws Exception {
            return null;
        }
    };

    @Test
    public void testCleanString() {
        assertTrue(service.cleanString("test").equals("test"));
        assertTrue(service.cleanString("test/1123").equals("test_1123"));
        assertTrue(service.cleanString("test 11,23").equals("test 11,23"));
    }

    @Test
    public void testconstructFileName() {
        House house1 = new House(1, "Test Villa", "Test", 11223, "http://localhost.com/test1.jpg");
        House house2 = new House(2, "Test Vi/lla", "Test2", 11233, "http://localhost.com/test1.png");
        House house3 = new House(4, "Test Villa,s", "Test3", 12133, "http://localhost.com/test2.trst.doc");
        assertTrue(service.constructFileName(house1).equals("1_Test Villa.jpg"));
        assertTrue(service.constructFileName(house2).equals("2_Test Vi_lla.png"));
        assertTrue(service.constructFileName(house3).equals("4_Test Villa,s.doc"));
    }

    @Test
    public void testDownloadImage() throws Exception {
        House house = new House(1, "Test Villa", "Test", 11223, "http://localhost.com/test1.jpg");
        doNothing().when(repository).persist(null, "1_Test Villa.jpg");
        service.downloadImage(house);
        Mockito.verify(repository, Mockito.times(1)).persist(null, "1_Test Villa.jpg");
    }

    @Test
    public void testRetrievePageContent() throws Exception {
        House house1 = new House(1, "Test Villa", "Test", 11223, "http://localhost.com/test1.jpg");
        House house2 = new House(2, "Test Vi/lla", "Test2", 11233, "http://localhost.com/test1.png");
        House house3 = new House(4, "Test Villa,s", "Test3", 12133, "http://localhost.com/test2.trst.doc");
        House[] houses = new House[] { house1, house2, house3 };
        ResponseEntity<HouseResponse> resp = (ResponseEntity<HouseResponse>) Mockito.mock(ResponseEntity.class);
        HouseResponse houseRes = Mockito.mock(HouseResponse.class);

        Mockito.when(template.getForEntity(serviceURL + 10, HouseResponse.class)).thenReturn(resp);
        Mockito.when(resp.getBody()).thenReturn(houseRes);
        Mockito.when(houseRes.houses()).thenReturn(houses);
        House[] result = service.retrievePageContent(10);
        assertTrue(result.equals(houses));
    }

    @Test
    public void testRetrieveContentFailure() throws Exception {
        try {

            serviceFail.retrieveContent(1, 10, 51, 50);
            // control should never reach here
            assertTrue(false);

        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
    }


}
