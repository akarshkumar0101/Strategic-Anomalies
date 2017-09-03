package game.sound;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
	/********************************************************
	 *  stepping.wav sound should be called for all knight movements
	 * 
	 * 
	 ********************************************************/
	
	/********************************************************
	 *  arrow2.wav sound should be called for all scouts attack (when successful)
	 * 
	 * 
	 ********************************************************/
	
	/********************************************************
	 *  block.wav sound should be called for all successful blocks (when successful).
	 *  Note: This is temporary. This block noise is specifically for units
	 *  with swords, but we will use this for all units atm.
	 * 
	 ********************************************************/
	
	/********************************************************
	 *  explode.wav should be called for assassin special, and any 
	 * fire attacks like pyromancer, dragon tyrants, etc.
	 * 
	 ********************************************************/
	
	/********************************************************
	 *  healing.wav should be called for all units who are healing 
	 * other units
	 * 
	 ********************************************************/
	/********************************************************
	 *  hit.wav should be called for all successful melee attacks
	 * 
	 * 
	 ********************************************************/
	
	/********************************************************
	 *  postclick.wav should be called for all clicks on buttons
	 * 
	 * 
	 ********************************************************/
	
	/********************************************************
	 *  soound15.wav is just for toggling noise while a player is browsing
	 * 
	 * 
	 ********************************************************/
	
	

	//This noise implements toggling noise 
    public static void toggleNoise() {
	/* insert sound into the game... Quinn */
	try {
		//Testing
		//Testing 12
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
    
    //this function implements noise after a player clicks a button 
    public static void successfulClickNoise() {
    	/* insert sound into the game... Quinn */
    	try {
    		//Testing
    		//Testing 12
    	    // Open an audio input stream.
    	    InputStream is = Sound.class.getResourceAsStream("/sound/postclick.wav");
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
    
    //This function implements any unit that blocks
    public static void blockNoise () {
    	
    	/* insert sound into the game... Quinn */
    	try {
    		//Testing
    		//Testing 12
    	    // Open an audio input stream.
    	    InputStream is = Sound.class.getResourceAsStream("/sound/block.wav");
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
    
    //This function implements a hit from a melee attack to any successful hit
    public static void hitNoise() {
    	
    	/* insert sound into the game... Quinn */
    	
    	try {
    		//Testing
    		//Testing 12
    	    // Open an audio input stream.
    	    InputStream is = Sound.class.getResourceAsStream("/sound/hit.wav");
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
