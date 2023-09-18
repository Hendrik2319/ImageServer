package com.example.imageserver;

import com.example.imageserver.admin.AdminInterface;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageServerApplication {

    public static void main(String[] args) {
        AdminInterface.setLookAndFeel();
        SpringApplication.run(ImageServerApplication.class, args);
    }

}
