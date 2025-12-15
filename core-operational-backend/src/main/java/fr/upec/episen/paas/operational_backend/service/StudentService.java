package fr.upec.episen.paas.operational_backend.service;

import java.time.LocalTime;
import java.time.ZoneId;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import fr.upec.episen.paas.operational_backend.dto.StudentDTO;
import fr.upec.episen.paas.operational_backend.model.Student;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final static Logger logger = LogManager.getLogger(StudentService.class);

    public Student getStudent(Long id) {
        String key = "student:" + id;
        try {
            if (!redisTemplate.hasKey(key)) {
                logger.info("Student with ID " + id + " not found in Redis.");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error accessing Redis for student with ID " + id + ": " + e.getMessage());
            return null;
        }
        Object studentObj = redisTemplate.opsForValue().get(key);
        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.convertValue(studentObj, Student.class);

        return student;
    }

    public StudentDTO getStudentDTO(Long id) {
        Student student = getStudent(id);
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setClassName("StudentService");
        if (student != null) {
            boolean allowed = false;
            LocalTime time = LocalTime.now(ZoneId.of("Europe/Paris"));
            if (time.isAfter(LocalTime.of(8, 0)) && time.isBefore(LocalTime.of(21, 0))) {
                allowed = "true".equalsIgnoreCase(student.getIsAuthorized());
            }
            studentDTO.setNum(student.getNum());
            studentDTO.setFirstname(student.getFirstname());
            studentDTO.setLastname(student.getLastname());
            studentDTO.setAllowed(allowed);
        } else {
            studentDTO.setAllowed(false);
        }
        return studentDTO;
    }
}