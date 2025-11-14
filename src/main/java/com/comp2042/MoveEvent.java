package com.comp2042;

/**
 * Immutable value object representing a game event with comprehensive metadata.
 * Contains event type, source, timestamp, and optional additional data.
 * 
 * <p>This class serves as the primary event communication mechanism between
 * different components of the Tetris game system.</p>
 * 
 * @author Dong, Jia.
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;
    private final long timestamp;
    private final Object additionalData;

    /**
     * Constructs a MoveEvent with the specified type and source.
     * Timestamp is automatically set to current system time.
     *
     * @param eventType   The type of the event.
     * @param eventSource The source of the event.
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this(eventType, eventSource, System.currentTimeMillis(), null);
    }

    /**
     * Constructs a MoveEvent with the specified type, source, and additional data.
     * Timestamp is automatically set to current system time.
     *
     * @param eventType      The type of the event.
     * @param eventSource    The source of the event.
     * @param additionalData Optional additional data associated with the event.
     */
    public MoveEvent(EventType eventType, EventSource eventSource, Object additionalData) {
        this(eventType, eventSource, System.currentTimeMillis(), additionalData);
    }

    /**
     * Constructs a MoveEvent with all parameters specified.
     *
     * @param eventType      The type of the event.
     * @param eventSource    The source of the event.
     * @param timestamp      The timestamp when the event occurred.
     * @param additionalData Optional additional data associated with the event.
     */
    public MoveEvent(EventType eventType, EventSource eventSource, long timestamp, Object additionalData) {
        this.eventType = eventType;
        this.eventSource = eventSource;
        this.timestamp = timestamp;
        this.additionalData = additionalData;
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

    /**
     * Gets the timestamp when the event occurred.
     *
     * @return The timestamp in milliseconds since epoch.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the additional data associated with the event.
     *
     * @return The additional data, or null if none.
     */
    public Object getAdditionalData() {
        return additionalData;
    }

    /**
     * Checks if this event is a movement event (LEFT, RIGHT, DOWN).
     *
     * @return true if the event is a movement event, false otherwise.
     */
    public boolean isMovementEvent() {
        return eventType == EventType.LEFT || eventType == EventType.RIGHT || eventType == EventType.DOWN;
    }

    /**
     * Checks if this event is a rotation event (ROTATE, ROTATE_CCW).
     *
     * @return true if the event is a rotation event, false otherwise.
     */
    public boolean isRotationEvent() {
        return eventType == EventType.ROTATE || eventType == EventType.ROTATE_CCW;
    }

    /**
     * Checks if this event is a drop event (HARD_DROP, SOFT_DROP).
     *
     * @return true if the event is a drop event, false otherwise.
     */
    public boolean isDropEvent() {
        return eventType == EventType.HARD_DROP || eventType == EventType.SOFT_DROP;
    }

    /**
     * Checks if this event originated from user input.
     *
     * @return true if the event source is USER, false otherwise.
     */
    public boolean isUserEvent() {
        return eventSource == EventSource.USER;
    }

    /**
     * Returns a string representation of this MoveEvent.
     *
     * @return A string representation of the event.
     */
    @Override
    public String toString() {
        return String.format("MoveEvent{type=%s, source=%s, timestamp=%d, data=%s}", 
                           eventType, eventSource, timestamp, additionalData);
    }

    /**
     * Checks if this event is equal to another object.
     *
     * @param obj The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MoveEvent moveEvent = (MoveEvent) obj;
        return timestamp == moveEvent.timestamp &&
               eventType == moveEvent.eventType &&
               eventSource == moveEvent.eventSource &&
               (additionalData != null ? additionalData.equals(moveEvent.additionalData) : 
                moveEvent.additionalData == null);
    }

    /**
     * Returns a hash code for this MoveEvent.
     *
     * @return A hash code for the event.
     */
    @Override
    public int hashCode() {
        int result = eventType.hashCode();
        result = 31 * result + eventSource.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (additionalData != null ? additionalData.hashCode() : 0);
        return result;
    }
}