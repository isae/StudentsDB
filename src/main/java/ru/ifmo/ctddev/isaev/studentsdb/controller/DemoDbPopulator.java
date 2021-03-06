package ru.ifmo.ctddev.isaev.studentsdb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.ctddev.isaev.studentsdb.dao.StudentDao;
import ru.ifmo.ctddev.isaev.studentsdb.entity.Student;
import ru.ifmo.ctddev.isaev.studentsdb.enums.EducationForm;
import ru.ifmo.ctddev.isaev.studentsdb.enums.GraduationType;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class DemoDbPopulator {
    private static String[] surnames = new String[] {
            "Иванов",
            "Васильев",
            "Петров",
            "Смирнов",
            "Михайлов",
            "Фёдоров",
            "Соколов",
            "Яковлев",
            "Попов",
            "Андреев",
            "Алексеев",
            "Александров",
            "Лебедев",
            "Григорьев",
            "Степанов",
            "Семёнов",
            "Павлов",
            "Богданов",
            "Николаев",
            "Дмитриев",
            "Егоров",
            "Волков",
            "Кузнецов",
            "Никитин",
            "Соловьёв",
            "Тимофеев",
            "Орлов",
            "Афанасьев",
            "Филиппов",
            "Сергеев",
            "Захаров",
            "Матвеев",
            "Виноградов",
            "Кузьмин",
            "Максимов",
            "Козлов",
            "Ильин",
            "Герасимов",
            "Марков",
            "Новиков",
            "Морозов",
            "Романов",
            "Осипов",
            "Макаров",
            "Зайцев",
            "Беляев",
            "Гаврилов",
            "Антонов",
            "Ефимов",
            "Леонтьев",
            "Давыдов",
            "Гусев",
            "Данилов",
            "Киселёв",
            "Сорокин",
            "Тихомиров",
            "Крылов"
    };

    private static String[] names = new String[] {
            "Александр",
            "Сергей",
            "Андрей",
            "Владимир",
            "Алексей",
            "Дмитрий",
            "Николай",
            "Евгений",
            "Иван",
            "Михаил",
            "Юрий",
            "Игорь",
            "Виктор",
            "Олег",
            "Павел",
            "Максим",
            "Василий",
            "Анатолий",
            "Виталий",
            "Роман",
            "Денис",
            "Валерий",
            "Константин",
            "Вячеслав",
            "Антон",
            "Вадим",
            "Илья",
            "Петр",
            "Владислав",
            "Геннадий",
            "Руслан",
            "Григорий",
            "Станислав",
            "Леонид",
            "Борис",
            "Артем",
            "Кирилл",
            "Никита",
            "Валентин",
            "Эдуард",
            "Георгий",
            "Федор",
            "Артур",
            "Степан",
            "Егор",
            "Ярослав",
            "Яков",
            "Богдан",
            "Даниил",
            "Тимур"
    };

    private static Random rand = new Random();

    @Autowired
    StudentDao dao;

    private String[] patronymics = new String[] {

            "Аланович", "Альбертович", "Анатольевич", "Арнольдович", "Аронович", "Артурович", "Валерьевич", "Вениаминович", "Владленович", "Германович", "Денисович", "Дмитриевич", "Елизарович", "Игоревич", "Иосифович", "Леонидович", "Львович", "Маркович", "Наумович", "Николаевич", "Олегович", "Рудольфович", "Станиславович", "Степанович", "Феликсович", "Эммануилович",
            "Александрович", "Вадимович", "Григорьевич", "Ефимович", "Максимович", "Натанович", "Павлович", "Ростиславович", "Федорович", "Эдуардович",
            "Алексеевич", "Васильевич", "Викторович", "Владимирович", "Евгеньевич", "Иванович", "Ильич", "Михайлович", "Петрович", "Сергеевич", "Юрьевич", "Яковлевич",
            "Андреевич", "Аркадьевич", "Артемович", "Валентинович", "Витальевич", "Матвеевич", "Никитович", "Платонович", "Романович", "Тимофеевич",
            "Антонович", "Богданович", "Богуславович", "Владиславович", "Вячеславович", "Геннадьевич", "Георгиевич", "Глебович", "Давидович", "Данилович", "Егорович", "Захарович", "Кириллович", "Константинович", "Макарович", "Миронович", "Никанорович", "Робертович", "Русланович", "Семенович", "Янович"
    };

    public void populate(int size) {
        for (int i = 0; i < size; ++i) {
            Student employee = new Student(
                    null,
                    names[rand.nextInt(names.length)],
                    surnames[rand.nextInt(surnames.length)],
                    patronymics[rand.nextInt(patronymics.length)],
                    randomDate(),
                    ThreadLocalRandom.current().nextInt(1950, 2017),
                    EducationForm.values()[rand.nextInt(3)],
                    GraduationType.values()[rand.nextInt(3)]
            );
            dao.save(employee);
        }
    }

    public LocalDate randomDate() {
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2015, 12, 31).toEpochDay();
        long randomDay = Math.abs(ThreadLocalRandom.current().nextLong(minDay, maxDay));
        return LocalDate.ofEpochDay(randomDay);
    }
}
