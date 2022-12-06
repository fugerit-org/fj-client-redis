package test;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisTest {

    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create( RedisURI.create("redis://192.168.5.87:6379") ) ;
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();            
        String value = commands.get("app-start");   
        System.out.println("Read value : "+value);
        connection.close();
        redisClient.shutdown();
    }
	
}
