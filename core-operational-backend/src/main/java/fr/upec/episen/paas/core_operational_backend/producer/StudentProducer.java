package fr.upec.episen.paas.core_operational_backend.producer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import paas.tp.common.backend.models.Student;

@Service
@RequiredArgsConstructor
public class StudentProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(StudentProducer.class);

    public void sendEntryAllowed(Student student) {
        logger.info("Sending entry allowed event for student: " + student.getId());
        kafkaTemplate.send("entrance-logs", student);
    }

    public void sendEntryLogs(Student student) {
        logger.info("Sending entry log event for student: " + student.getId());
        kafkaTemplate.send("logs", student);
    }
}
