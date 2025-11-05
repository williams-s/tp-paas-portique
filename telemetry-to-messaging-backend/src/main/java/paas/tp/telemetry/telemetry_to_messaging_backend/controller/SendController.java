package paas.tp.telemetry.telemetry_to_messaging_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paas.tp.common.backend.DTO.StudentDTO;

@RestController
@RequestMapping("/api/send")
public class SendController {

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    @PostMapping
    public String send(@RequestBody Long id) {
        kafkaTemplate.send("attemps-logs", id);
        kafkaTemplate.send("logs", id);
        return "success";
    }
}
