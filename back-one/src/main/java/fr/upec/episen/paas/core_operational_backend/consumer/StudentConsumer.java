package fr.upec.episen.paas.core_operational_backend.consumer;

import java.sql.Timestamp;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.upec.episen.paas.core_operational_backend.dto.StudentDTO;
import fr.upec.episen.paas.core_operational_backend.producer.StudentProducer;
import fr.upec.episen.paas.core_operational_backend.service.CoreApiClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentConsumer {
    private static final Logger logger = LogManager.getLogger(StudentConsumer.class);
    private final StudentProducer studentProducer;
    private final CoreApiClient coreApiClient;

    @KafkaListener(topics = "entrance-logs", groupId = "core-operational-backend")
    public void consumeStudentEntranceEvent(String object) {

        logger.info("Received student entrance event: " + object);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        StudentDTO studentDTO = new StudentDTO();
        try {
            jsonNode = mapper.readTree(object);
            Long studentId = jsonNode.get("studentId").asLong();
            Long doorId = jsonNode.get("doorId").asLong();
            String doorName = jsonNode.get("doorName").asText();
            studentDTO = coreApiClient.fetchStudent(studentId);

            studentDTO.setDoorId(doorId);
            studentDTO.setDoorName(doorName);
            studentDTO.setTimestamp(Timestamp.from(Instant.now()));

            if (studentDTO.isAllowed()) {
                logger.info("Student with ID " + studentId + " is allowed to enter through the door " + doorId + ".");
            } else {
                logger.info(
                        "Student with ID " + studentId + " is not allowed to enter through the door " + doorId + ".");
            }

            studentProducer.sendEntry(studentDTO);
            studentProducer.sendEntryLogs(studentDTO);
        } catch (Exception e) {
            logger.error("Error processing student attempt event: " + e.getMessage());
            studentProducer.sendEntryLogs(studentDTO);
            return;
        }
    }
}
