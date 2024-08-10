package org.fugerit.java.client.redis;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.fugerit.java.core.util.ObjectUtils;

import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class ClientRedisHelper implements AutoCloseable {

	public static final Long TTL_UNDEFINED = Long.valueOf( -1 );
	
	private String redisUrl;
	
	private RedisClient redisClient;
	
	private Long timeToLive;
	
	private ClientRedisHelper( String redisUrl, Long timeToLive ) {
		this.redisUrl = redisUrl;
		this.redisClient = RedisClient.create( RedisURI.create( redisUrl ) );
		this.timeToLive = ObjectUtils.objectWithDefault( timeToLive, TTL_UNDEFINED );
	}
	
	@Override
	public void close() {
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

	public List<String> listKeys() {
		List<String> list;
		try ( StatefulRedisConnection<String, String> connection = this.redisClient.connect() ) {
			RedisCommands<String, String> commands = connection.sync();
			KeyScanCursor<String> scan = commands.scan();
			list = scan.getKeys();
		}
		return list;
	}
	
	public List<Entry<String, String>> all() {
		List<Entry<String, String>> list = new ArrayList<>();
		try ( StatefulRedisConnection<String, String> connection = this.redisClient.connect() ) {
			RedisCommands<String, String> commands = connection.sync();
			KeyScanCursor<String> scan = commands.scan();
			for ( String key : scan.getKeys() ) {
				list.add( new AbstractMap.SimpleEntry<>( key, this.get(key) ) );
			}			
		}
		return list;
	}
	
	public void set( String key, String value )  {
		this.set(key, value, this.timeToLive);
	}
	
	public void set( String key, String value, Long ttl )  {
		try ( StatefulRedisConnection<String, String> connection = this.redisClient.connect() ) {
			RedisCommands<String, String> commands = connection.sync();			
			if ( ttl != null && !ttl.equals( TTL_UNDEFINED ) ) {
				commands.setex( key , ttl, value ); 
			} else {
				commands.set( key, value );
			}
		}
	}

	public String get( String key)  {
		String value = null;
		try ( StatefulRedisConnection<String, String> connection = this.redisClient.connect() ) {
			RedisCommands<String, String> commands = connection.sync();
			value = commands.get( key );
		}
		return value;
	}
	
	public long del( String key )  {
		long value = 0;
		try ( StatefulRedisConnection<String, String> connection = this.redisClient.connect() ) {
			RedisCommands<String, String> commands = connection.sync();
			value = commands.del( key );
		}
		return value;
	}
	
	public Long getTTL( String key)  {
		Long time = null;
		try ( StatefulRedisConnection<String, String> connection = this.redisClient.connect() ) {
			RedisCommands<String, String> commands = connection.sync();
			time = commands.ttl( key );
		}
		return time;
	}

	public String serverInfo()  {
		try ( StatefulRedisConnection<String, String> connection = this.redisClient.connect() ) {
			RedisCommands<String, String> commands = connection.sync();
			return commands.info();
		}
	}
	
	public static ClientRedisHelper newHelper( String redisUrl, Long timeToLive )  {
		return new ClientRedisHelper(redisUrl, timeToLive);
	}
	
	public static ClientRedisHelper newHelper( String redisUrl )  {
		return newHelper(redisUrl, TTL_UNDEFINED);
	}
	
}
