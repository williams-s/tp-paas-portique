package paas.tp.entrance_cockpit_backend.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedList;
import java.util.Queue;

public class QueueEntrance {

    public final static Queue<JsonNode> ENTRANCES = new LinkedList<>();
    public final static int MAX_ENTRANCES_HISTORY = 10;

    public static void addAcceptedEntrance(JsonNode jsonNode) {
        ENTRANCES.add(jsonNode);
        if (ENTRANCES.size() > MAX_ENTRANCES_HISTORY)  {
            ENTRANCES.poll();
        }
    }
}
