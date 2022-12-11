package org.fugerit.java.client.redis;

import java.util.Properties;

import org.fugerit.java.client.redis.gui.ClientRedisGUI;
import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRedisRun extends ClientRedisArgs {

	private static final Logger logger = LoggerFactory.getLogger( ClientRedisRun.class );
		
	public static void main( String[] args ) {
		try {
			Properties params = ArgUtils.getArgs( args );
			String mode = params.getProperty( ARG_MODE, MODE_DEFAULT );
			if ( MODE_GUI.equalsIgnoreCase( mode ) ) {
				new ClientRedisGUI( params );
			} else if ( MODE_SINGLE_COMMAND.equalsIgnoreCase( mode ) ) {
				String redisUrl = params.getProperty( ARG_REDIS_URL );
				String ttl = params.getProperty( ARG_TTL, ClientRedisHelper.TTL_UNDEFINED.toString() );
				String key = params.getProperty( ARG_KEY );
				String value = params.getProperty( ARG_VALUE );
				logger.info( "redis client  params -> redis-url:{}, ttl:{}", redisUrl, ttl );
				logger.info( "redis command params -> key:{}, value:{}", key, value );
				if ( StringUtils.isEmpty( redisUrl ) || StringUtils.isEmpty( key ) ) {
					throw new ConfigException( "In mode '"+mode+"' the following params are required : "+ARG_REDIS_URL+" , "+ARG_KEY );
				} else {
					try ( ClientRedisHelper client = ClientRedisHelper.newHelper(redisUrl, Long.valueOf( ttl ) ) ) {
						logger.info( " ************************************************************************************************" );
						if ( StringUtils.isNotEmpty( value ) ) {
							logger.info( " * SET MODE key:{} value:{}", key, value );
							client.set(key, value);
						} else {
							logger.info( " * GET MODE key:{}", key );
							value = client.get(key);
						}
						logger.info( " ************************************************************************************************" );
						logger.info( " * value for key {} : {} *", key, value );
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
