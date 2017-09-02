package testing;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sounds {

    public static Sound beepSound;

    public static Sound headlinesSongSound;

    static {
	try {

	    Sounds.beepSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/sound15.wav"));
	    Sounds.headlinesSongSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/headlines.wav"));

	} catch (Exception e) {
	    throw new RuntimeException("Could not load sounds", e);
	}
    }

    public static void playSound(Sound sound) {
	// sound.playSound();
    }

    // public static void repeatSound(Sound sound) {
    // Clip clip = sound.playSound();
    // Sounds.setVolume(clip, 1f);
    // }
    //
    // private static float getVolume(Clip clip) {
    // FloatControl gainControl = (FloatControl)
    // clip.getControl(FloatControl.Type.MASTER_GAIN);
    // return (float) Math.pow(10f, gainControl.getValue() / 20f);
    // }
    //
    // private static void setVolume(Clip clip, float volume) {
    // if (volume < 0f || volume > 1f) {
    // throw new IllegalArgumentException("Volume not valid: " + volume);
    // }
    // FloatControl gainControl = (FloatControl)
    // clip.getControl(FloatControl.Type.MASTER_GAIN);
    // gainControl.setValue(20f * (float) Math.log10(volume));
    // //
    // }
}

class Sound {
    private final AudioInputStream audioInputStream;
    private final AudioFormat audioFormat;

    private final int size_in_bytes;
    private final byte[] audioData;

    public Sound(InputStream inputStream) throws Exception {
	audioInputStream = AudioSystem.getAudioInputStream(inputStream);
	audioFormat = audioInputStream.getFormat();

	size_in_bytes = (int) (audioFormat.getFrameSize() * audioInputStream.getFrameLength());

	audioData = new byte[size_in_bytes];
	audioInputStream.read(audioData, 0, size_in_bytes);
    }

    Clip playSound() {
	try {
	    Clip clip = AudioSystem.getClip();
	    clip.open(audioFormat, audioData, 0, size_in_bytes);
	    clip.start();
	    return clip;
	} catch (Exception e) {
	    throw new RuntimeException("Error playing sound", e);
	}
    }
}
