package org.thingml.tradfri;

import java.util.Random;

public class Main {

	protected static String gateway_ip = "10.0.1.19";
	protected static String security_key = "CoEe2XtT5LPkXPe4";
	private static Random r = new Random();

	public static void main( String[] args ) {

		TradfriGateway gw = new TradfriGateway( gateway_ip, security_key );
		gw.initCoap();
		gw.dicoverBulbs();

		for( LightBulb b : gw.bulbs ) {
			if( !b.getName().equals( "TRADFRI bulb E27 CWS opal 600lm" ) ) {
				b.setColor( TradfriConstants.COLOR_COLD );
				generateLightningBolt( b );
			}
		}

		System.exit( 0 );
	}

	private static void generateLightningBolt( LightBulb b ) {
		final int numberOfFlashes = 7;

		final int minTimeBetweenFlashesMS = 50;
		final int maxTimeBetweenFlashesMS = 150;

		final int minIntensity = 50;
		final int maxIntensity = 250;

		for( int i = 0 ; i < numberOfFlashes ; i++ ) {
			try {
				final long waitTimeMS = generateRandom( minTimeBetweenFlashesMS, maxTimeBetweenFlashesMS );
				final int intensity = generateRandom( minIntensity, maxIntensity );

				System.out.println( String.format( "Flash %s; Intensity %s; Delay: %s", i, waitTimeMS, intensity ) );

				b.setIntensity( intensity, 0 );
				Thread.sleep( waitTimeMS );
				b.setIntensity( 0, 0 );
			}
			catch( InterruptedException e ) {
				throw new RuntimeException( "Dammit, don't interrupt me while I'm lightninging!" );
			}
		}
	}

	private static int generateRandom( int min, int max ) {
		return r.nextInt( max - min + 1 ) + min;
	}
}