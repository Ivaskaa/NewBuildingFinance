package com.example.NewBuildingFinance.service.staticService;

import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StaticService {

    /**
     * create random file name and save file in directory
     * @param directory directory
     * @param file file
     * @param fileName file name
     * @throws IOException
     */
    void savePhoto(String directory, MultipartFile file, String fileName) throws IOException;

    /**
     * delete file from directory with filename
     * @param directory directory
     * @param fileName filename
     */
    void deletePhoto(String directory, String fileName);

    /**
     * get string with (and) and create sort object
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @return sort object
     */
    Sort sort(String sortingField, String sortingDirection);
}
