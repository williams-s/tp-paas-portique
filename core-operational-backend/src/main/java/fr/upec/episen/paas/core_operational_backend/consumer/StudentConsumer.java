package fr.upec.episen.paas.core_operational_backend.consumer;

import java.sql.Timestamp;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import fr.upec.episen.paas.core_operational_backend.dto.StudentDTO;
import fr.upec.episen.paas.core_operational_backend.models.Student;
import fr.upec.episen.paas.core_operational_backend.producer.StudentProducer;
import fr.upec.episen.paas.core_operational_backend.service.StudentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentConsumer {
    private static final Logger logger = LogManager.getLogger(StudentConsumer.class);

    private final StudentService studentService;
    private final StudentProducer studentProducer;

    @KafkaListener(topics = "attemps-logs", groupId = "core-operational-backend")
    public void consumeStudentAttemptEvent(Long id) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setClassName("StudentConsumer");
        studentDTO.setTimestamp(timestamp);
        try {
            Student student = studentService.getStudentIfAllowed(id);
            studentDTO.setId(student.getId());
            studentDTO.setFirstname(student.getFirstname());
            studentDTO.setLastname(student.getLastname());
            logger.info("Retrieved student: " + student);
            if (student != null && student.isShouldOpen()) {
                studentDTO.setAllowed(true);
                studentDTO.setStatus("OK");
                studentProducer.sendEntryAllowed(studentDTO);
                studentProducer.sendEntryLogs(studentDTO);
                logger.info("Student with ID " + id + " is allowed to enter.");
            } else {
                studentDTO.setAllowed(false);
                studentDTO.setStatus("OK");
                studentProducer.sendEntryLogs(studentDTO);
                logger.info("Student with ID " + id + " is not allowed to enter.");
            }
        } catch (Exception e) {
            studentDTO.setId(null);
            studentDTO.setFirstname(null);
            studentDTO.setLastname(null);
            studentDTO.setAllowed(false);
            studentDTO.setStatus("KO");
            studentProducer.sendEntryLogs(studentDTO);
            logger.error("Error processing student with ID " + id, e);
        }
    }
}
