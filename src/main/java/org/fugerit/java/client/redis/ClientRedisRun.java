package org.fugerit.java.client.redis;

import java.util.Properties;

import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class ClientRedisRun {

	private static final Logger logger = LoggerFactory.getLogger( ClientRedisRun.class );
	
	public static final String ARG_MODE = "mode";
	public static final String MODE_TEST = "test";
	public static final String MODE_GUI = "gui";
	public static final String MODE_DEFAULT = MODE_TEST;
	
	public static final String ARG_REDIS_URL = "redis-url";
	public static final String ARG_TEST_KEY = "test-key";
	
	public static void main( String[] args ) {
		try {
			Properties params = ArgUtils.getArgs( args );
			String mode = params.getProperty( ARG_MODE, MODE_DEFAULT );
			if ( MODE_TEST.equalsIgnoreCase( mode ) ) {
				String redisUrl = params.getProperty( ARG_REDIS_URL );
				String testKey = params.getProperty( ARG_TEST_KEY );
				if ( StringUtils.isEmpty( redisUrl ) || StringUtils.isEmpty( redisUrl ) ) {
					throw new ConfigException( "In mode '"+mode+"' the following params are required : "+ARG_REDIS_URL+" , "+ARG_TEST_KEY );
				} else {
					try ( RedisClient redisClient = RedisClient.create( RedisURI.create( redisUrl ) );
							StatefulRedisConnection<String, String> connection = redisClient.connect() ) {
						 RedisCommands<String, String> commands = connection.sync();            
					     String value = commands.get( testKey );   
					     logger.info( " ************************************************************************************************" );
					     logger.info( " * value for key {} : {} *", testKey, value );
					     logger.info( " ************************************************************************************************" );
					     logger.info( " * CONNECTION SUCCESSFUL !!!                                                                    *" );
					     logger.info( " ************************************************************************************************" );
					}
				}
			} else {
				logger.warn( "{} not supported {}", ARG_MODE, mode );
			}
		} catch (Exception e) {
			logger.info( "Error : "+e, e );
		}
	}
	
}
