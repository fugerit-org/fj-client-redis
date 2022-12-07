package org.fugerit.java.client.redis;

import org.fugerit.java.core.util.ObjectUtils;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class ClientRedisHelper implements AutoCloseable {

	public static final Long TTL_UNDEFINED = Long.valueOf( -1 );
	
	private String redisUrl;
	
	private RedisClient redisClient;
	
	private Long timeToLive;
	
	private ClientRedisHelper( String redisUrl, Long timeToLive ) throws ClientRedisException {
		this.redisUrl = redisUrl;
		this.redisClient = RedisClient.create( RedisURI.create( redisUrl ) );
		this.timeToLive = ObjectUtils.objectWithDefault( timeToLive, TTL_UNDEFINED );
	}
	
	@Override
	public void close() throws ClientRedisException {
		this.redisUrl = null;
		this.timeToLive = null;
		this.redisClient.shutdown();
	}

	public String getRedisUrl() {
		return redisUrl;
	}

	public Long getTimeToLive() {
		return timeToLive;
	}

	public void set( String key, String value ) throws ClientRedisException {
		try ( StatefulRedisConnection<String, String> connection = this.redisClient.connect() ) {
			RedisCommands<String, String> commands = connection.sync();			
			commands.set( key , value );
			if ( this.timeToLive != null && this.timeToLive != TTL_UNDEFINED ) {
				commands.expire(key,this.timeToLive );
			}
		}
	}

	public String get( String key) throws ClientRedisException {
		String value = null;
		try ( StatefulRedisConnection<String, String> connection = this.redisClient.connect() ) {
			RedisCommands<String, String> commands = connection.sync();
			value = commands.get( key );
		}
		return value;
	}
	
	public static ClientRedisHelper newHelper( String redisUrl, Long timeToLive ) throws ClientRedisException {
		return new ClientRedisHelper(redisUrl, timeToLive);
	}
	
	public static ClientRedisHelper newHelper( String redisUrl ) throws ClientRedisException {
		return newHelper(redisUrl, TTL_UNDEFINED);
	}
	
}
