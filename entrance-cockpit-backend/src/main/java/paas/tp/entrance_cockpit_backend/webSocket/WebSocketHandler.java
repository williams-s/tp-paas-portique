package paas.tp.entrance_cockpit_backend.webSocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import paas.tp.entrance_cockpit_backend.utils.QueueEntrance;

import java.util.HashSet;
import java.util.Set;

public class WebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LogManager.getLogger(WebSocketHandler.class);

    private static final Set<WebSocketSession> sessions = new HashSet<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("Client connected : " + session.getId());
        for (JsonNode jsonNode : QueueEntrance.ENTRANCES) {
            sendMessage(jsonNode.toString());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("Client disconnected : " + session.getId());
    }

    public void sendMessage(String message) {
        for (WebSocketSession session : sessions) {
            try {
                logger.info("Sending message to session " + session.getId());
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                logger.error("Error sending message to session " + session.getId(), e);
            }
        }
    }
}
