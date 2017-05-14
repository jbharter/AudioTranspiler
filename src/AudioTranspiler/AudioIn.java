package AudioTranspiler;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.util.function.Function;

public class AudioIn {
    private static final float SAMPLING_RATE = 44100;
    private static final int SAMPLE_BIT_DEPTH = 16;
    private static final int CHANNELS = 2;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;

    static AudioFormat getAudioformat() {

        return new AudioFormat(SAMPLING_RATE,SAMPLE_BIT_DEPTH,CHANNELS,SIGNED,BIG_ENDIAN);
    }

    private static Function<Track,Boolean> writeOut = track -> {
        try {
            Runnable r = new FileWriter(track);
            Thread t = new Thread(r);
            t.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    };
    private static Function<byte[],Integer> quietness = arr -> {
        byte max = arr[0];
        byte min = arr[0];
        for (byte b : arr) { max = (b > max) ? b : max; min = (b < min) ? b : min; }
        return (min >= -10 && max <= 10) ? (min >=  -5 && max <=  5) ? (min >=  -2 && max <=  2) ? (min >=  -1 && max <=  1) ? (min == max && min ==  0) ? 0 : 1 : 2 : 3 : 4 : 5;
    };

    public AudioIn() throws Exception {

        TargetDataLine line;
        SourceDataLine output;

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, AudioIn.getAudioformat()); // format is an AudioFormat object
        DataLine.Info oinfo = new DataLine.Info(SourceDataLine.class, AudioIn.getAudioformat());
        if (!AudioSystem.isLineSupported(info)) { throw(new Exception("Eh?")); }
        line = (TargetDataLine) AudioSystem.getLine(info);
        output = (SourceDataLine) AudioSystem.getLine(oinfo);

        line.open(AudioIn.getAudioformat());
        output.open(AudioIn.getAudioformat());
        ByteArrayOutputStream out  = new ByteArrayOutputStream();
        ByteArrayOutputStream in   = new ByteArrayOutputStream();
        int numBytesRead;
        int onbr;
        byte[] data = new byte[line.getBufferSize() / 5];
        // Begin audio capture.
        line.start();
        output.start();
        boolean quiet = false;
        long quietCount = 0;
        Function<Long,Boolean> run = param -> param < 2000;
        Track t = new Track();

        while (run.apply(quietCount)) {
            // Read the next chunk of data from the TargetDataLine.
            numBytesRead =  line.read(data, 0, data.length);
            onbr = output.write(data,0,data.length);
            // Save this chunk of data.
            out.write(data, 0, numBytesRead);
            in.write(data,0,onbr);
            if (quietness.apply(data) <= 3) {
                ++quietCount;
                if (!quiet && t.size() > 0) {
                    quietCount = 0L;
                    AudioIn.writeOut.apply(t);
                    t = new Track();
                }
                quiet = true;
            } else {
                t.append(data);
                quiet = false;
            }
        }
    }

    public static void main (String[] args) throws Exception {
        new AudioIn();
    }
}
