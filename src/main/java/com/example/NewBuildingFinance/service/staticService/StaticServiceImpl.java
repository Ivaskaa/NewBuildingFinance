package com.example.NewBuildingFinance.service.staticService;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@Log4j2
public class StaticServiceImpl implements StaticService{

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void savePhoto(String directory, MultipartFile file, String fileName) throws IOException {
        String slash = "\\";
        if(System.getProperty("os.name").contains("Linux")){
            slash = "/";
        }
        File path = new File(uploadPath + slash + directory);
        if(!path.exists()){
            path.mkdirs();
        }
        file.transferTo(new File(path + slash + fileName));
        log.info("new photo in " + directory + " directory");
    }

    @Override
    public void deletePhoto(String directory, String fileName) {
        String slash = "\\";
        if(System.getProperty("os.name").contains("Linux")){
            slash = "/";
        }
        File file = new File(uploadPath + slash + directory + slash + fileName);
        if(file.exists()){
            file.delete();
        } else {
            log.warn("Exception delete photo in " + directory + " directory, file doesn't exist");
        }
        log.info("delete photo in " + directory + " directory");
    }


    public Sort sort(String sortingField, String sortingDirection) {
        Sort sort;
        if(sortingField.contains(" and ")){
            sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField.split(" and "));
        } else {
            sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        }
        return sort;
    }
}