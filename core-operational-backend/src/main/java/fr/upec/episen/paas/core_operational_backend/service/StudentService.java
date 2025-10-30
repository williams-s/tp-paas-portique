package fr.upec.episen.paas.core_operational_backend.service;

import java.time.LocalTime;
import java.time.ZoneId;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import paas.tp.common.backend.models.Student;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final static Logger logger = LogManager.getLogger(StudentService.class);

    public Student getStudentIfAllowed(Long id) {
        LocalTime time = LocalTime.now(ZoneId.of("Europe/Paris"));
        if (time.isBefore(LocalTime.of(8,0)) || time.isAfter(LocalTime.of(21,0))) {
            return null;
        }

        String key = "student:" + id;
        Object studentObj = redisTemplate.opsForValue().get(key);
        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.convertValue(studentObj, Student.class);

        if (student.isShouldOpen()) {
            logger.info("Student {} is allowed to open the door.", student.toString());
            return student;
        }
        return null;
    }
}