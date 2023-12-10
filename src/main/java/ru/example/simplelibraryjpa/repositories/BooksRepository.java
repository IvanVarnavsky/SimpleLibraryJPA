package ru.example.simplelibraryjpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.simplelibraryjpa.models.Book;
import ru.example.simplelibraryjpa.models.Person;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    // Получение списка книг по читателю
    List<Book> findByReader(Person reader);

    // Поиск книг по первым буквам названии без учёта регистра
    List<Book> findByTitleStartingWithIgnoreCase(String title);
}
