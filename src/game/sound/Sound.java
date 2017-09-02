package game.sound;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

    public static void playSound() {
	/* insert sound into the game... Quinn */
	try {
		//Testing
	    // Open an audio input stream.
	    InputStream is = Sound.class.getResourceAsStream("/sound/sound15.wav");
	    AudioInputStream audioIn = AudioSystem.getAudioInputStream(is);
	    // Get a sound clip resource.
	    Clip clip = AudioSystem.getClip();
	    // Open audio clip and load samples from the audio input stream.
	    clip.open(audioIn);
	    clip.start();
	} catch (UnsupportedAudioFileException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (LineUnavailableException e) {
	    e.printStackTrace();
	}
    }

}
