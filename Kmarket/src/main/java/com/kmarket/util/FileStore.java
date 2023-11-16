package com.kmarket.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 파일 실제 경로에 저장.
 * @return 원래 파일명과, 서버에 저장될 파일명
 */
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    /**
     * 저장할 전체 경로 가져오기
     */
    public String getFullPath(String storeFileName) {
        return fileDir + storeFileName;
    }

    /**
     * product 파일 저장용
     */
    public String getFullPath(String storeFileName, Integer cate1, Integer cate2) {
        return fileDir + cate1 + "/" + cate2 + "/" + storeFileName;
    }

    /**
     * 파일 저장
     */
    public String storeFile(MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename(); // 업로드한 파일명
        String storeFileName = createStoreFileName(originalFileName); // 서버에 저장할 파일명 생성
        multipartFile.transferTo(new File(getFullPath(storeFileName))); // 경로에 파일 저장

        return storeFileName;
    }

    /**
     * product 파일 저장용
     */
    public String storeFile(MultipartFile multipartFile, Integer cate1, Integer cate2) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename(); // 업로드한 파일명
        String storeFileName = createStoreFileName(originalFileName); // 서버에 저장할 파일명 생성
        multipartFile.transferTo(new File(getFullPath(storeFileName, cate1, cate2))); // 경로에 파일 저장

        return storeFileName;
    }

    /**
     * 서버에 저장할 이름 만들기
     */
    private String createStoreFileName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    /**
     * 확장자 가져오기
     */
    private String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }
}
