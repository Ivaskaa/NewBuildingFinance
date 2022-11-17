package com.example.NewBuildingFinance.service.secureToken.staticService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StaticService {

    void savePhoto(String directory, MultipartFile file, String fileName) throws IOException;

    void deletePhoto(String directory, String fileName);
}
