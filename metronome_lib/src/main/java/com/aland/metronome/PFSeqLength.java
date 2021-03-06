package com.aland.metronome;

public class PFSeqLength {
    public static final int MODE_FRACTIONAL = 0;
    public static final int MODE_ABSOLUTE = 1;

    private PFSeqService seq;
    private PFSeqTimeOffset timeOffset;
    private int mode; // one of the PFSeqLength.MODE_ constants
    private long lengthAbsoluteNano;

    public PFSeqLength(PFSeqService seq, int mode, PFSeqTimeOffset timeOffset, long lengthAbsoluteNano) {
        this.seq = seq;
        this.timeOffset = timeOffset;
        this.mode = mode;
        this.lengthAbsoluteNano = lengthAbsoluteNano;
    }

    public long getLengthFrames() {
        switch (mode) {
            case MODE_ABSOLUTE:
                return seq.nanoToFrames(lengthAbsoluteNano);
            case MODE_FRACTIONAL:
                double nanosPerBeat = seq.getNanosecondsPerBeat().doubleValue();
                int beats = timeOffset.getBeats();
                long offsetFromBeatNano = (long) ( timeOffset.getPercent() * nanosPerBeat );
                long lengthNano = (long) (beats * nanosPerBeat) + offsetFromBeatNano;
                return seq.nanoToFrames(lengthNano);
        }

        return -1;
    }
}
