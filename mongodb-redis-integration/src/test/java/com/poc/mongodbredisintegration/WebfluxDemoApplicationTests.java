/**
 * Copyright (c) Raja Dilip Chowdary Kolli. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.poc.mongodbredisintegration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.poc.mongodbredisintegration.document.Book;
import com.poc.mongodbredisintegration.repository.BookReactiveRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Raja Kolli
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class WebfluxDemoApplicationTests extends AbstractMongoDBRedisIntegrationTest {

	private static final String TITLE = "JUNIT_TITLE";

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private BookReactiveRepository bookReactiveRepository;

	@BeforeAll
	public void setUp() {
		final List<Book> bookList = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			final Book book = new Book();
			book.setTitle(TITLE + String.valueOf(i));
			bookList.add(book);
		}
		this.bookReactiveRepository.saveAll(bookList);
	}

	@Test
	public void testCreateBook() {
		final Book book = Book.builder().author("Raja").text("This is a Test Book")
				.title("JUNIT_TITLE").build();

		this.webTestClient.post().uri("/Books")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8).body(Mono.just(book), Book.class)
				.exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON_UTF8).expectBody()
				.jsonPath("$.id").isNotEmpty().jsonPath("$.text")
				.isEqualTo("This is a Test Book");
	}

	@Test
	@DisplayName("Invalid Data")
	public void testCreateBookFail() {
		Book book = Book.builder().author("Raja").text("This is a Test Book")
				.title(RandomStringUtils.randomAlphanumeric(200)).build();

		this.webTestClient.post().uri("/Books")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8).body(Mono.just(book), Book.class)
				.exchange().expectStatus().isBadRequest().expectHeader()
				.contentType(MediaType.APPLICATION_JSON_UTF8).expectBody()
				.jsonPath("$.message")
				.isEqualTo("Validation failed for object='book'. Error count: 1")
				.jsonPath("$.errors.[0].defaultMessage")
				.isEqualTo("size must be between 0 and 140");

		book = Book.builder().build();
		this.webTestClient.post().uri("/Books")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8).body(Mono.just(book), Book.class)
				.exchange().expectStatus().isBadRequest().expectHeader()
				.contentType(MediaType.APPLICATION_JSON_UTF8).expectBody()
				.jsonPath("$.message")
				.isEqualTo("Validation failed for object='book'. Error count: 1")
				.jsonPath("$.errors.[0].defaultMessage").isEqualTo("must not be blank");
	}

	@Test
	public void testGetAllBooks() {
		this.webTestClient.get().uri("/Books").accept(MediaType.APPLICATION_JSON_UTF8)
				.exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON_UTF8).expectBodyList(Book.class);
	}

	@Test
	public void testGetSingleBook() {
		final Book book = this.bookReactiveRepository.save(Book.builder().author("Raja")
				.text("This is a Test Book").title("JUNIT_TITLE").build()).block();

		this.webTestClient.get()
				.uri("/Books/{id}", Collections.singletonMap("id", book.getId()))
				.exchange().expectStatus().isOk().expectBody().consumeWith(
						(response) -> assertThat(response.getResponseBody()).isNotNull());
	}

	@Test
	public void testUpdateBook() {
		final Book book = this.bookReactiveRepository.save(Book.builder().author("Raja")
				.text("This is a Test Book").title("JUNIT_TITLE").build()).block();

		final Book newBookData = Book.builder().author("Raja").text("Updated Book")
				.title("JUNIT_TITLE").build();

		this.webTestClient.put()
				.uri("/Books/{id}", Collections.singletonMap("id", book.getId()))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.body(Mono.just(newBookData), Book.class).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8).expectBody()
				.jsonPath("$.text").isEqualTo("Updated Book");
	}

	@Test
	public void testDeleteBook() {
		final Book book = this.bookReactiveRepository.save(Book.builder().author("Raja")
				.text("This is a Test Book").title("JUNIT_TITLE").build()).block();

		this.webTestClient.delete()
				.uri("/Books/{id}", Collections.singletonMap("id", book.getId()))
				.exchange().expectStatus().isOk();
	}

	@Test
	public void testActuatorStatus() {
		this.webTestClient.get().uri("/actuator/health")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBody().json("{\"status\":\"UP\"}");
	}

}
