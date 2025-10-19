package com.comp2042;

/**
 * Value object (DTO) representing a specific game event related to moving or rotating a brick.
 * Contains the type of event and its source.
 */
public final class MoveEvent {
    private final EventType eventType;  // The type of the event (DOWN, LEFT, RIGHT, ROTATE)
    private final EventSource eventSource; // The source of the event (USER, THREAD)

    /**
     * Constructs a MoveEvent with the specified type and source.
     *
     * @param eventType   The type of the event.
     * @param eventSource The source of the event.
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Gets the type of the event.
     *
     * @return The EventType.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Gets the source of the event.
     *
     * @return The EventSource.
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}