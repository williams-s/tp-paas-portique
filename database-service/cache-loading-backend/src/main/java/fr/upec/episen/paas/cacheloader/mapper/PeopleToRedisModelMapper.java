package fr.upec.episen.paas.cacheloader.mapper;

import java.util.List;

import fr.upec.episen.paas.cacheloader.model.People;
import fr.upec.episen.paas.cacheloader.model.Student;

public class PeopleToRedisModelMapper {

    public List<Student> peopleToStudent(List<People> people, boolean shouldOpen) {
        List<Student> students = people.stream().map(p -> {
            Student student = new Student();
            student.setNum(p.getNum());
            student.setFirstname(p.getFirstName());
            student.setLastname(p.getLastName());
            student.setShouldOpen(shouldOpen);
            return student;
        }).toList();

        return students;
    }
}
