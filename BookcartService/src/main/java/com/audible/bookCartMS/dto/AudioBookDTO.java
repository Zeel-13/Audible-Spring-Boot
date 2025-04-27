package com.audible.bookCartMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioBookDTO {
	private int bookId;

    private String title;

    private String author;

    private String narrator;

    private int time;

    private String release_date;

    private String language;

    private double stars;

    private double price;
    private int ratings;
}