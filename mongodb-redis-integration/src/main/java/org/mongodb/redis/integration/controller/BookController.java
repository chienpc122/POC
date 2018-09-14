package org.mongodb.redis.integration.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mongodb.redis.integration.document.Book;
import org.mongodb.redis.integration.exception.BookNotFoundException;
import org.mongodb.redis.integration.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

	private final BookService bookService;

	@GetMapping("/findByTitle/{title}")
	public Book findBookByTitle(@NotNull @PathVariable String title)
			throws BookNotFoundException {
		return this.bookService.findBookByTitle(title);
	}

	@PostMapping("/saveBook")
	public Book saveBook(@Valid @RequestBody Book book) {
		return this.bookService.saveBook(book);
	}

	@PutMapping("/updateByTitle/{title}/{author}")
	public Book updateAuthorByTitle(@PathVariable("title") String title,
			@PathVariable("author") String author) {
		return this.bookService.updateAuthorByTitle(title, author);
	}

	@DeleteMapping("/deleteByTitle/{title}")
	public ResponseEntity<String> deleteBookByTitle(@PathVariable("title") String title)
			throws BookNotFoundException {
		this.bookService.deleteBook(title);
		return ResponseEntity.accepted().body("Book with title " + title + " deleted");
	}

	/**
	 * Deletes all cache.
	 */
	@GetMapping("/deleteCache")
	public String deleteCache() {
		return this.bookService.deleteAllCache();
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	private void bookNotFoundHandler(BookNotFoundException ex) {
		log.error("Entering and leaving BookController : bookNotFoundHandler ",
				ex.getMessage());
	}

}
