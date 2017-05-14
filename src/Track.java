package AudioTranspiler;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

class Track {
    Track() { }

    private class Packet implements Runnable {
        private final byte[] packet;

        Packet(byte[] pack) {

            packet = pack;
        }

        @Override
        public void run() {
            for (byte b : packet) track.add(b);
        }
    }
    private volatile ArrayList<Byte> track = new ArrayList<>();

    long size() {
        return track.size();
    }
    void append(byte[] appendee) {
        Runnable r = new Packet(appendee);
        Thread t = new Thread(r);
        t.run();
    }
    ByteArrayOutputStream getOutputStream() {
        ByteArrayOutputStream ret = new ByteArrayOutputStream();
        for (Byte b : track) ret.write(b);
        return ret;
    }
    ByteArrayInputStream getInputStream() {
        return new ByteArrayInputStream(getOutputStream().toByteArray());
    }
}
