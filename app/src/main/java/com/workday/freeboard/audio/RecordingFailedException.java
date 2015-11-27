package com.workday.freeboard.audio;

import java.io.IOException;

/**
 * Created by brian.odriscoll on 27/11/2015.
 */
public class RecordingFailedException extends RuntimeException {
    public RecordingFailedException(String s, IOException io) {
        super(s, io);
    }
}
