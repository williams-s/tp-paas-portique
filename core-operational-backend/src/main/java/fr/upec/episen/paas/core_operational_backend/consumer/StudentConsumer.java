package fr.upec.episen.paas.core_operational_backend.consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.upec.episen.paas.core_operational_backend.producer.StudentProducer;
import fr.upec.episen.paas.core_operational_backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import paas.tp.common.backend.DTO.StudentDTO;
import paas.tp.common.backend.models.Student;

@Service
@RequiredArgsConstructor
public class StudentConsumer {
    private static final Logger logger = LogManager.getLogger(StudentConsumer.class);

    private final StudentService studentService;
    private final StudentProducer studentProducer;

    @KafkaListener(topics = "attemps-logs", groupId = "core-operational-backend")
    public void consumeStudentAttemptEvent(StudentDTO studentDTO) {
        Long id;
        try {
            id = studentDTO.id();
        } catch (Exception e) {
            return;
        }
        try {
            Student student = studentService.getStudentIfAllowed(id);
            logger.info("Retrieved student: " + student);
            if (student != null && student.isShouldOpen()) {
                studentProducer.sendEntryAllowed(student);
                studentProducer.sendEntryLogs(student);
                logger.info("Student with ID " + id + " is allowed to enter.");
            } else {
                studentProducer.sendEntryLogs(student);
                logger.info("Student with ID " + id + " is not allowed to enter.");
            }
        } catch (Exception e) {
            logger.error("Failed to parse message: " + studentDTO, e);
        }

    }
}
