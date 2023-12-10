package ru.example.simplelibraryjpa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.example.simplelibraryjpa.models.Book;
import ru.example.simplelibraryjpa.models.Person;
import ru.example.simplelibraryjpa.services.BooksService;
import ru.example.simplelibraryjpa.services.PeopleService;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;
    private final PeopleService peopleService;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(@RequestParam(required = false, value = "page", defaultValue = "0") Integer page,
                        @RequestParam(required = false, value = "books_per_page") Integer booksPerPage,
                        @RequestParam(required = false, value = "sort_by_year", defaultValue = "false") boolean sortByYear,
                        Model model) {
        if (booksPerPage == null)
            model.addAttribute("books", booksService.findAll(sortByYear));
        else
            model.addAttribute("books", booksService.findAll(page, booksPerPage, sortByYear));
        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        Book book = booksService.findOne(id);
        model.addAttribute("book", book);
        if (book.getReader() == null)
            model.addAttribute("people", peopleService.findAll());
        else
            model.addAttribute("person", peopleService.findOne(book.getReader().getId()));
        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "books/new";

        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findOne(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "books/edit";

        booksService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/give")
    public String giveBook(@ModelAttribute("person") Person person, @PathVariable("id") int id) {
        booksService.give(id, person);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/free")
    public String freeBook(@PathVariable("id") int id) {
        booksService.free(id);
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String search(@ModelAttribute("book") Book book) {
        return "books/search";
    }

    @PostMapping("/search")
    public String search(Model model, @RequestParam("query") String query) {
        model.addAttribute("books", booksService.findByBookTitle(query));
        return "books/search";
    }
}
