package ru.example.simplelibraryjpa.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.example.simplelibraryjpa.models.Person;
import ru.example.simplelibraryjpa.services.PeopleService;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;

        // Проверка уникальности ФИО
        if (peopleService.getPersonByFIO(person.getFio()) != null && peopleService.findOne(person.getId()) == null) {
            errors.rejectValue("fio", "", "Человек с таким ФИО уже существует");
        }

        // Проверяем, что у человека ФИО начинается с заглавной буквы
        if (!Character.isUpperCase(person.getFio().codePointAt(0)))
            errors.rejectValue("fio", "", "ФИО должно начинаться с большой буквы");

    }
}
