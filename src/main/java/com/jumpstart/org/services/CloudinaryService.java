package com.jumpstart.org.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Cloudinary cloudinary = Singleton.getCloudinary();

    /**
     * Upload A MultipleFile To Cloudinary
     */
    public String upload(MultipartFile file){
        try{
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String publicId = uploadResult.get("public_id").toString();
            logger.info("The user " + "successfully uploaded the file: " + publicId);
            return cloudinary.url().publicId(publicId).generate();
        } catch (Exception ex) {
            logger.error("The user " + " failed to load to Cloudinary the image file: " + file.getName());
            logger.error(ex.getMessage());
            return null;
        }
    }
}
