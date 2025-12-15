package fr.upec.episen.paas.core_operational_backend.producer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import fr.upec.episen.paas.core_operational_backend.dto.StudentDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(StudentProducer.class);

    public void sendEntry(StudentDTO studentDTO) {
        logger.info("Sending entry allowed event for student: " + studentDTO);
        kafkaTemplate.send("attemps-logs", studentDTO);
    }

    public void sendEntryLogs(StudentDTO studentDTO) {
        logger.info("Sending entry log event for student: " + studentDTO);
        kafkaTemplate.send("logs", studentDTO);
    }
}
