package com.kumarmanoj.bookhive.book;

import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.kumarmanoj.bookhive.userbooks.UserBooks;
import com.kumarmanoj.bookhive.userbooks.UserBooksPrimaryKey;
import com.kumarmanoj.bookhive.userbooks.UserBooksRepository;

@Controller
public class BookController {
    private final String COVER_IMAGE_ROUTE = "https://covers.openlibrary.org/b/id/";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserBooksRepository userBooksRepository;

    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            String coverImageUrl = "/images/no-image.png";
            if (book.getCoverIds() != null && book.getCoverIds().size() > 0) {
                coverImageUrl = COVER_IMAGE_ROUTE + book.getCoverIds().get(0) + "-L.jpg";
            }
            model.addAttribute("converImageUrl", coverImageUrl);
            model.addAttribute("book", book);

            if (principal != null && principal.getAttribute("login") != null) {
                model.addAttribute("loginId", principal.getAttribute("login"));
                UserBooksPrimaryKey primaryKey = new UserBooksPrimaryKey();
                primaryKey.setUserId(principal.getAttribute("login"));
                primaryKey.setBookId(bookId);
                Optional<UserBooks> userBooks = userBooksRepository.findById(primaryKey);
                if (userBooks.isPresent()) {
                    model.addAttribute("userBooks", userBooks.get());
                } else {
                    model.addAttribute("userBooks", new UserBooks());
                }
            }

            return "book";
        }

        return "book-not-found";
    }
}
