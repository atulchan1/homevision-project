package com.homevision.homecontent.repository;

import java.io.InputStream;
import org.springframework.stereotype.Repository;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Repository
public class FileRepository {
    public static final String PARENT_DIR = "images/";

    public void persist(InputStream in, String fileName) throws Exception {
        Files.copy(in, Paths.get(PARENT_DIR + fileName), StandardCopyOption.REPLACE_EXISTING);
    }

}