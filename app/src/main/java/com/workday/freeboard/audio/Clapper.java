package com.workday.freeboard.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

import root.gast.audio.record.AmplitudeClipListener;
import root.gast.audio.util.AudioUtil;

public class Clapper
{
    private static final String TAG = "Clapper";

    private static final long DEFAULT_CLIP_TIME = 1000;
    private long clipTime = DEFAULT_CLIP_TIME;
    private AmplitudeClipListener clipListener;

    private boolean continueRecording;

    /**
     * how much louder is required to hear a clap 10000, 18000, 25000 are good
     * values
     */
    private int amplitudeThreshold;

    /**
     * requires a little of noise by the user to trigger, background noise may
     * trigger it
     */
    public static final int AMPLITUDE_DIFF_LOW = 10000;
    public static final int AMPLITUDE_DIFF_MED = 18000;
    /**
     * requires a lot of noise by the user to trigger. background noise isn't
     * likely to be this loud
     */
    public static final int AMPLITUDE_DIFF_HIGH = 25000;

    private static final int DEFAULT_AMPLITUDE_DIFF = AMPLITUDE_DIFF_MED;

    private MediaRecorder recorder;

    private String tmpAudioFile;

    public Clapper()
    {
        this(DEFAULT_CLIP_TIME, Environment.getExternalStorageDirectory() + "/tmp.3gp", DEFAULT_AMPLITUDE_DIFF, null, null);
    }

    public Clapper(long snipTime, String tmpAudioFile,
                   int amplitudeDifference, Context context, AmplitudeClipListener clipListener)
    {
        this.clipTime = snipTime;
        this.clipListener = clipListener;
        this.amplitudeThreshold = amplitudeDifference;
        this.tmpAudioFile = tmpAudioFile;
    }

    public boolean recordClap()
    {
        Log.d(TAG, "record clap");
        boolean clapDetected = false;

        try
        {
            recorder = AudioUtil.prepareRecorder(tmpAudioFile);
        }
        catch (IOException io)
        {
            Log.d(TAG, "failed to prepare recorder ", io);
            throw new RecordingFailedException("failed to create recorder", io);
        }

        recorder.start();
        int startAmplitude = recorder.getMaxAmplitude();
        Log.d(TAG, "starting amplitude: " + startAmplitude);

        do
        {
            Log.d(TAG, "waiting while recording...");
            waitSome();
            int finishAmplitude = recorder.getMaxAmplitude();
            if (clipListener != null)
            {
                clipListener.heard(finishAmplitude);
            }

            int ampDifference = finishAmplitude - startAmplitude;
            if (ampDifference >= amplitudeThreshold)
            {
                Log.d(TAG, "heard a clap!");
                clapDetected = true;
            }
            Log.d(TAG, "finishing amplitude: " + finishAmplitude + " diff: "
                    + ampDifference);
        } while (continueRecording || !clapDetected);

        Log.d(TAG, "stopped recording");
        done();

        return clapDetected;
    }

    private void waitSome()
    {
        try
        {
            // wait a while
            Thread.sleep(clipTime);
        } catch (InterruptedException e)
        {
            Log.d(TAG, "interrupted");
        }
    }

    /**
     * need to call this when completely done with recording
     */
    public void done()
    {
        Log.d(TAG, "stop recording");
        if (recorder != null)
        {
            if (isRecording())
            {
                stopRecording();
            }
            //now stop the media player
            recorder.stop();
            recorder.release();
        }
    }

    public boolean isRecording()
    {
        return continueRecording;
    }

    public void stopRecording()
    {
        continueRecording = false;
    }
}