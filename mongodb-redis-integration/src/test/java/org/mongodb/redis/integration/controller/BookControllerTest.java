package org.mongodb.redis.integration.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mongodb.redis.integration.document.Book;
import org.mongodb.redis.integration.exception.BookNotFoundException;
import org.mongodb.redis.integration.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BookService bookService;

	@Test
	public void getBook_ShouldReturnBook() throws Exception {
		given(bookService.findBookByTitle(anyString()))
				.willReturn(Book.builder().title("JUNIT_TITLE").author("JUNIT_AUTHOR")
						.bookId("JUNIT").text("JUNIT_TEXT").version(1L).build());

		this.mockMvc.perform(MockMvcRequestBuilders.get("/book/findByTitle/JUNIT_TITLE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("title").value("JUNIT_TITLE"))
				.andExpect(jsonPath("author").value("JUNIT_AUTHOR"));
	}

	@Test
	public void getBookNotFound() throws Exception {
		given(bookService.findBookByTitle(anyString()))
				.willThrow(new BookNotFoundException());

		mockMvc.perform(MockMvcRequestBuilders.get("/book/findByTitle/MongodbBook"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void saveBookShouldReturnBook() throws Exception {
		Book book = Book.builder().title("JUNIT_TITLE").author("JUNIT_AUTHOR")
				.bookId("JUNIT").text("JUNIT_TEXT").build();
		given(bookService.saveBook(ArgumentMatchers.any(Book.class))).willReturn(book);

		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/book/saveBook")
						.content(this.objectMapper.writeValueAsString(book))
						.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("title").value("JUNIT_TITLE"))
				.andExpect(jsonPath("author").value("JUNIT_AUTHOR"));
	}

	@Test
	public void updateBookShouldReturnBook() throws Exception {
		Book book = Book.builder().title("MongoDbCookBook").author("JUNIT_AUTHOR")
				.bookId("JUNIT").text("JUNIT_TEXT").build();
		given(bookService.updateAuthorByTitle(ArgumentMatchers.eq("MongoDbCookBook"),
				anyString())).willReturn(book);

		this.mockMvc
				.perform(MockMvcRequestBuilders
						.put("/book/updateByTitle/MongoDbCookBook/Raja1")
						.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("title").value("MongoDbCookBook"))
				.andExpect(jsonPath("author").value("JUNIT_AUTHOR"));
	}

}