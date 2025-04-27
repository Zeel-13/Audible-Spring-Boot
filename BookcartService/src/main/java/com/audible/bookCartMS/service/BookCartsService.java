package com.audible.bookCartMS.service;

import java.util.List;

import com.audible.bookCartMS.dto.AudioBookDTO;
import com.audible.bookCartMS.model.BookCart;

public interface BookCartsService {
	public List<AudioBookDTO> getCartAudiobooks(int cartId);
	BookCart addBookCart(int userId, List<Integer> audiobookIds);
}
