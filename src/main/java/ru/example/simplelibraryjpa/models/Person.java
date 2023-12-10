package ru.example.simplelibraryjpa.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "Person")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "ФИО не должно быть пустым")
    @Size(min = 2, max = 90, message = "ФИО должно быть от 2 до 90 символов длиной")
    @Column(name = "fio")
    private String fio;

    @Min(value = 1800, message = "Год рождения должен быть больше, чем 1800")
    @Column(name = "birth_year")
    private int birthYear;

    @OneToMany(mappedBy = "reader")
    private List<Book> books;

    // Конструктор по умолчанию нужен для Spring
    public Person() {
    }

    public Person(String fio, int birthYear) {
        this.fio = fio;
        this.birthYear = birthYear;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
