package ru.example.simplelibraryjpa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.simplelibraryjpa.models.Book;
import ru.example.simplelibraryjpa.models.Person;
import ru.example.simplelibraryjpa.repositories.BooksRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    // Поиск всех книг с сортировкой по году
    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear)
            return booksRepository.findAll(Sort.by("year"));
        else
            return booksRepository.findAll();
    }

    // Поиск всех книг с сортировкой по году и пагинацией
    public List<Book> findAll(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear)
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    // Поиск книг по названию (нескольким первым буквам названия)
    public List<Book> findByBookTitle(String title) {
        return booksRepository.findByTitleStartingWithIgnoreCase(title);
    }

    public Book findOne(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);
        return foundBook.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        Optional<Book> foundBook = booksRepository.findById(id);
        if (foundBook.isPresent()) {
            updatedBook.setId(id);
            updatedBook.setReader(foundBook.get().getReader());
            updatedBook.setTakeBookTime(foundBook.get().getTakeBookTime());
            booksRepository.save(updatedBook);
        }
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    // Назначает книгу читателю (этот метод вызывается, когда читатель забирает книгу из библиотеки)
    @Transactional
    public void give(int id, Person reader) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setReader(reader);
                    book.setTakeBookTime(new Date());
                });
    }

    // Освобождает книгу (этот метод вызывается, когда читатель возвращает книгу в библиотеку)
    @Transactional
    public void free(int id) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setReader(null);
                    book.setTakeBookTime(null);
                });
    }

    // Получение списка книг по id читателя
    public List<Book> getBooksByPerson(Person person) {
        List<Book> books = booksRepository.findByReader(person);
        LocalDateTime now = LocalDateTime.now();
        for (Book book : books){
            LocalDateTime personBookTime = book.getTakeBookTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (now.minusDays(10).isAfter(personBookTime))
                book.setOverdue(true); // проставляем признак просроченной книги
        }
        return books;
    }
}
