package org.thingml.tradfri;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {

	private static final String BASE_AUDIO_PATH = "/Users/hugi/Desktop/sound/";
	protected static String gateway_ip = "10.0.1.19";
	protected static String security_key = "CoEe2XtT5LPkXPe4";
	private static Random r = new Random();

	private static final String BULB_COLOR = "TRADFRI bulb E27 CWS opal 600lm";
	private static final String BULB_WHITE = "TRADFRI bulb E27 WS opal 980lm";

	public static void main( String[] args ) {

		// new Thread( () -> {
		// playRainscape();
		// } ).start();

		final LightBulb bulb = bulbNamed( BULB_WHITE );
		bulb.setColor( TradfriConstants.COLOR_COLD );

		while( true ) {
			try {
				if( System.in.available() > 0 ) {
					char keyChar = (char)System.in.read();
					System.out.println( "Boom!" );
					triggerLightningAndSound( bulb );
				}
			}
			catch( IOException e ) {
				throw new RuntimeException( e );
			}
		}

		// System.exit( 0 );
	}

	private static void triggerLightningAndSound( LightBulb bulb ) {
		playThunderclap();
		generateLightningBolt( bulb );
	}

	private static LightBulb bulbNamed( String bulbName ) {
		TradfriGateway gw = new TradfriGateway( gateway_ip, security_key );
		gw.initCoap();
		gw.dicoverBulbs();

		for( LightBulb b : gw.bulbs ) {
			System.out.println( b.getName() );
			if( b.getName().equals( bulbName ) ) {
				return b;
			}
		}

		return null;
	}

	private static void generateLightningBolt( LightBulb bulb ) {
		final int numberOfFlashes = 7;

		final int minFlashTimeInMS = 10;
		final int maxFlashTimeInMS = 30;

		final int minTimeBetweenFlashesMS = 50;
		final int maxTimeBetweenFlashesMS = 130;

		final int minIntensity = 50;
		final int maxIntensity = 250;

		bulb.setColor( TradfriConstants.COLOR_COLD );

		for( int i = 0 ; i < numberOfFlashes ; i++ ) {
			try {
				final long timeBetweenFlashesMS = generateRandom( minTimeBetweenFlashesMS, maxTimeBetweenFlashesMS );
				final int flashTimeInMS = generateRandom( minFlashTimeInMS, maxFlashTimeInMS );
				final int intensity = generateRandom( minIntensity, maxIntensity );

				System.out.println( String.format( "Flash %s; Intensity %s; Delay: %s", i, timeBetweenFlashesMS, intensity ) );

				bulb.setIntensity( intensity, 0 );
				Thread.sleep( flashTimeInMS );
				bulb.setIntensity( 0, 0 );
				Thread.sleep( timeBetweenFlashesMS );
			}
			catch( InterruptedException e ) {
				throw new RuntimeException( "Dammit, don't interrupt me while I'm lightninging!" );
			}
		}
	}

	public static void playThunderclap() {
		playAudioFile( "thunder" );
	}

	public static void playRainscape() {
		playAudioFile( "rain" );
	}

	public static void playAudioFile( String filename ) {
		try {
			final AudioInputStream audioIn = AudioSystem.getAudioInputStream( new BufferedInputStream( new FileInputStream( new File( BASE_AUDIO_PATH + filename + ".wav" ) ) ) );
			Clip clip = AudioSystem.getClip();
			clip.open( audioIn );
			clip.start();
		}
		catch( UnsupportedAudioFileException | IOException | LineUnavailableException e ) {
			throw new RuntimeException( e );
		}
	}

	private static int generateRandom( int min, int max ) {
		return r.nextInt( max - min + 1 ) + min;
	}
}