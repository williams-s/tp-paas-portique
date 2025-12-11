package fr.upec.episen.paas.operational_backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.upec.episen.paas.operational_backend.dto.StudentDTO;
import fr.upec.episen.paas.operational_backend.service.StudentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/operational_backend")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private static final Logger logger = LogManager.getLogger(StudentController.class);
    
    @GetMapping("/student/{id}")
    public StudentDTO openDoorForStudent(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentDTO(id);
        if (student != null) {
            return studentService.getStudentDTO(id);
        } else {
            return null;
        }
    }
}
