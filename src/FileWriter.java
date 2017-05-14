package AudioTranspiler;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;

public class FileWriter implements Runnable {
    private final Track track;

    FileWriter(Track t) {

        this.track = t;
    }

    @Override
    public void run() {

        String fileName = Long.toString(this.track.size()) + ".wav";
        AudioInputStream ais = new AudioInputStream( track.getInputStream(), AudioIn.getAudioformat(), (track.size())/AudioIn.getAudioformat().getFrameSize());

        if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, ais)) {
            try { AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(fileName)); }
            catch (IOException e) { e.printStackTrace(); }
        }
        System.out.println("wrote: " + fileName);
    }
}
