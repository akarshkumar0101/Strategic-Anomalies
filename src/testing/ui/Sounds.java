package testing.ui;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sounds {

    public static Sound headlinesSongSound;

    // sound15.wav is just for toggling noise while a player is browsingThis noise
    // implements toggling noise
    public static Sound beepSound;
    // postclick.wav should be called for all clicks on buttons
    public static Sound postClickSound;

    // hit.wav should be called for all successful melee attacks
    public static Sound hitSound;
    // block.wav sound should be called for all successful blocks (when successful).
    // Note: This is temporary. This block noise is specifically for units
    // with swords, but we will use this for all units atm.
    public static Sound blockSound;

    // healing.wav should be called for all units who are healing other units
    public static Sound healingSound;
    // explode.wav should be called for assassin special, and any fire attacks like
    // pyromancer, dragon tyrants, etc.
    public static Sound explodingSound;

    // arrow2.wav sound should be called for all scouts attack (when successful)
    public static Sound arrowSound;

    static {
	try {
	    Sounds.headlinesSongSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/headlines.wav"));

	    Sounds.beepSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/sound15.wav"));
	    Sounds.postClickSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/postclick.wav"));

	    Sounds.hitSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/hit.wav"));
	    Sounds.blockSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/block.wav"));
	    Sounds.healingSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/healing.wav"));
	    Sounds.explodingSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/explode.wav"));
	    Sounds.arrowSound = new Sound(Sounds.class.getResourceAsStream("/temp_sounds/arrow2.wav"));

	} catch (Exception e) {
	    throw new RuntimeException("Could not load sounds", e);
	}
    }

    public static void playSound(Sound sound) {
	sound.playSound();
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
