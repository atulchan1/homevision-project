package com.homevision.homecontent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record House(int id, String address, String homeowner, long price, String photoURL) {
}
