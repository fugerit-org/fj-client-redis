package org.fugerit.java.client.redis;

import java.util.Properties;

import org.fugerit.java.client.redis.gui.ClientRedisGUI;
import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRedisRun {

	private static final Logger logger = LoggerFactory.getLogger( ClientRedisRun.class );
		
	private static final String STAR_LINE = " ************************************************************************************************";
	
	public static void main( String[] args ) {
		try {
			Properties params = ArgUtils.getArgs( args );
			String mode = params.getProperty( ClientRedisArgs.ARG_MODE, ClientRedisArgs.MODE_DEFAULT );
			if ( ClientRedisArgs.MODE_GUI.equalsIgnoreCase( mode ) ) {
				new ClientRedisGUI( params );
			} else if ( ClientRedisArgs.MODE_SINGLE_COMMAND.equalsIgnoreCase( mode ) ) {
				String redisUrl = params.getProperty( ClientRedisArgs.ARG_REDIS_URL );
				String ttl = params.getProperty( ClientRedisArgs.ARG_TTL, ClientRedisHelper.TTL_UNDEFINED.toString() );
				String key = params.getProperty( ClientRedisArgs.ARG_KEY );
				String value = params.getProperty( ClientRedisArgs.ARG_VALUE );
				String list = params.getProperty( ClientRedisArgs.ARG_LIST );
				logger.info( "redis client  params -> redis-url:{}, ttl:{}", redisUrl, ttl );
				logger.info( "redis command params -> key:{}, value:{}", key, value );
				if ( StringUtils.isEmpty( redisUrl ) || ( StringUtils.isEmpty( key ) && StringUtils.isEmpty( list ) ) ) {
					throw new ConfigException( "In mode '"+mode+"' the following params are required : "+ClientRedisArgs.ARG_REDIS_URL+" and ( "+ClientRedisArgs.ARG_KEY+" or "+ClientRedisArgs.ARG_LIST_ALL+" )" );
				} else {
					try ( ClientRedisHelper client = ClientRedisHelper.newHelper(redisUrl, Long.valueOf( ttl ) ) ) {
						logger.info( STAR_LINE );
						if ( StringUtils.isNotEmpty( list ) ) {
							if ( ClientRedisArgs.ARG_LIST_ALL.equalsIgnoreCase( list ) ) {
								client.all().stream().forEach( e -> logger.info( " * SET MODE list all:{} current:{}", e ) );
							} else {
								client.listKeys().stream().forEach( k -> logger.info( " * SET MODE list keys:{} current:{}", k ) );
							}
						} else {
							if ( StringUtils.isNotEmpty( value ) ) {
								logger.info( " * SET MODE key:{} value:{}", key, value );
								client.set(key, value);
							} else {
								logger.info( " * GET MODE key:{}", key );
								value = client.get(key);
							}
							logger.info( STAR_LINE );
							logger.info( " * value for key {} : {} *", key, value );
							logger.info( STAR_LINE);
						}
					    logger.info( " * CONNECTION SUCCESSFUL !!!                                                                    *" );
					    logger.info( STAR_LINE );
					}
				}
			} else {
				logger.warn( "{} not supported {}", ClientRedisArgs.ARG_MODE, mode );
			}
		} catch (Exception e) {
			logger.info( "Error : "+e, e );
		}
	}
	
}
