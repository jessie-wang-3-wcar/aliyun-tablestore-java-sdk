package com.alicloud.openservices.tablestore.timeline;

import com.alicloud.openservices.tablestore.timeline.model.TimelineEntry;
import com.alicloud.openservices.tablestore.timeline.model.TimelineIdentifier;
import com.alicloud.openservices.tablestore.timeline.model.TimelineMessage;
import com.alicloud.openservices.tablestore.timeline.query.ScanParameter;

import java.util.Iterator;
import java.util.concurrent.Future;

/**
 * The queue of single timeline distinguished by identifier.
 */
public interface TimelineQueue {

    /**
     * The specified identifier of single timeline queue.
     *
     * @return Identifier
     */
    TimelineIdentifier getIdentifier();

    /**
     * Store message into specified timeline queue with auto-generated sequence id.
     *
     * @param message       The content of the message to store.
     *
     * @return TimelineEntry
     */
    TimelineEntry store(TimelineMessage message);

    /**
     * Store message to specified timeline queue whose sequence id is set manually.
     *
     * @param sequenceId    The sequence id of the timeline, which should be unique and incremental.
     * @param message       The content of the message to store.
     *
     * @return TimelineEntry
     */
    TimelineEntry store(long sequenceId, TimelineMessage message);

    /**
     * Store message asynchronously with auto-generated sequence id.
     *
     * @param message       The content of the message to store.
     * @param callback      The timeline callback, which deal with response.
     *
     * @return Future<TimelineEntry>
     */
    Future<TimelineEntry> storeAsync(TimelineMessage message, TimelineCallback callback);

    /**
     * Store message asynchronously with manually set sequence id.
     *
     * @param sequenceId    The sequence id of the timeline, which should be unique and incremental.
     * @param message       The content of the message to store.
     * @param callback      The timeline callback, which deal with response.
     *
     * @return Future<TimelineEntry>
     */
    Future<TimelineEntry> storeAsync(long sequenceId, TimelineMessage message, TimelineCallback callback);

    /**
     * Batch store message to specified timeline queue with auto-generated sequence id.
     *
     * @param message       The content of the message to store.
     *
     * @return Future<TimelineEntry>
     */
    Future<TimelineEntry> batchStore(TimelineMessage message);

    /**
     * Batch store message asynchronously with manually set sequence id.
     *
     * @param sequenceId    The sequence id of the timeline, which should be unique and incremental.
     * @param message       The content of the message to store.
     *
     * @return Future<TimelineEntry>
     */
    Future<TimelineEntry> batchStore(long sequenceId, TimelineMessage message);

    /**
     * Store message asynchronously with autogenerated sequence id by writer.
     *
     * @param message       The content of the message to store.
     * @param callback      Timeline callback, which deal with single message response.
     *
     * @return Future<TimelineEntry>
     */
    Future<TimelineEntry> batchStore(TimelineMessage message, TimelineCallback callback);

    /**
     * Store message asynchronously with manually set sequence id by writer.
     *
     * @param sequenceId    The sequence id of the timeline, which should be unique and incremental.
     * @param message       The content of the message to store.
     * @param callback      Timeline callback, which deal with single message response.
     *
     * @return Future<TimelineEntry>
     */
    Future<TimelineEntry> batchStore(long sequenceId, TimelineMessage message, TimelineCallback callback);

    /**
     * Update message with new content by sequence id.
     *
     * @param sequenceId    The sequence id of the timeline to update.
     * @param message       New content of the message to update.
     *
     * @return TimelineEntry
     */
    TimelineEntry update(long sequenceId, TimelineMessage message);
    /**
     * Update message asynchronously with new content by sequence id.
     *
     * @param sequenceId    The sequence id of the timeline to update.
     * @param message       New content of the message to update.
     * @param callback      Timeline callback to deal with response.
     *
     * @return Future<TimelineEntry>
     */
    Future<TimelineEntry> updateAsync(long sequenceId, TimelineMessage message, TimelineCallback callback);

    /**
     * Get timeline entry by sequence id.
     * Return null if this timeline entry is not exist.
     *
     * @param sequenceId    The sequence id of the timeline to get.
     *
     * @return TimelineEntry
     */
    TimelineEntry get(long sequenceId);

    /**
     * Delete timeline entry by specified sequence id.
     *
     * @param sequenceId    The sequence id of the timeline to delete.
     */
    void delete(long sequenceId);

    /**
     * Scan a specified range of timeline entries by scan parameter.
     *
     * @param parameter     The parameter of scan range.
     *
     * @return Iterator<TimelineEntry>
     */
    Iterator<TimelineEntry> scan(ScanParameter parameter);

    /**
     * Get the latest sequence id of specified identifier.
     * Return 0 if not exist.
     *
     * @return long
     */
    long getLatestSequenceId();

    /**
     * Get the latest timeline entry of specified identifier.
     * Return null if not exist.
     *
     * @return TimelineEntry
     */
    TimelineEntry getLatestTimelineEntry();

    /**
     * Flush all the messages in buffer, wait until finish writing.
     */
    void flush();

    void close();
}
