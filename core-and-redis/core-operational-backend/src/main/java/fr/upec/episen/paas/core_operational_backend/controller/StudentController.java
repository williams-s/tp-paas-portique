package fr.upec.episen.paas.core_operational_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.upec.episen.paas.core_operational_backend.dto.StudentDTO;
import fr.upec.episen.paas.core_operational_backend.service.StudentService;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/core_operational_backend/students")
@RequiredArgsConstructor
public class StudentController {

    @Autowired
    private final StudentService studentService;

    private static final Logger logger = LogManager.getLogger(StudentController.class);

    @GetMapping("/attempts/{id}")
    public StudentDTO openDoorForStudent(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentDTO(id);
        if (student != null) {
            logger.info("Student with ID " + id + " is allowed to enter.");
            return studentService.getStudentDTO(id);
        } else {
            logger.info("Student with ID " + id + " is not allowed to enter.");
            return null;
        }
    }

}
