package com.popytka.popytka.models;

import jakarta.persistence.*;
import org.apache.tomcat.util.codec.binary.Base64;

@Entity
public class Country {

    @Lob
    private byte[] image;

    public String generateBase64Image() {
        return Base64.encodeBase64String(this.image);
    }
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY  )
    private Long country_id;
    private String country_name;

    public Country() {
    }

    public Long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(Long country_id) {
        this.country_id = country_id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }
}
