package org.fugerit.java.client.redis;

import lombok.extern.slf4j.Slf4j;
import org.fugerit.java.core.function.SafeFunction;
import org.fugerit.java.core.lang.helpers.StringUtils;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Redis command functions
 */
@Slf4j
public abstract class ClientRedisFun {

    private static final String VALUE_FOR_KEY_LIT = "Value for key '";

    private static final String HAS_BEEN_SET_TO_LIT = "' has been set to '";

    private static final String ERROR_GETTING_VALUE_FOR_JEY_LIT = "Error getting value for key=";

    private String redisUrl;

    protected ClientRedisFun(String redisUrl ) {
        this.redisUrl = redisUrl;
    }

    public Consumer<Exception> getExceptionConsumer( String message ) {
        return e -> this.error( message, e );
    }

    public ClientRedisHelper getHelper() {
        return ClientRedisHelper.newHelper(redisUrl);
    }


    public void error( String baseMessage, Exception e ) {
        log.error( String.format( "%s : %s" , baseMessage, e.getMessage() ), e );
    }

    public abstract String getKey();

    public abstract String getValue();

    public abstract String getTTL();

    public abstract void outputLine(String line);

    protected void executeStart( String message ) {
        log.info( "Execute : {}", message );
    }

    public void set() {
        this.executeStart( "set" );
        String key = this.getKey();
        String value = this.getValue();
        SafeFunction.apply( () -> {
            if ( StringUtils.isEmpty( key ) || StringUtils.isEmpty( value ) ) {
                this.outputLine("Required parameters : key, value");
            } else {
                try (ClientRedisHelper client = this.getHelper()) {
                    String ttl = this.getTTL();
                    if ( StringUtils.isNotEmpty( ttl ) ) {
                        long time = Long.parseLong( ttl );
                        client.set(key, value, time);
                        this.outputLine(VALUE_FOR_KEY_LIT+key+HAS_BEEN_SET_TO_LIT+value+"' and ttl="+time+"(s)");
                    } else {
                        client.set(key, value);
                        this.outputLine(VALUE_FOR_KEY_LIT+key+HAS_BEEN_SET_TO_LIT+value+"'");
                    }
                }
            }
        }, this.getExceptionConsumer( ERROR_GETTING_VALUE_FOR_JEY_LIT+key ) );
    }

    public void get() {
        this.executeStart( "get" );
        String key = this.getKey();
        SafeFunction.apply( () -> {
            if ( StringUtils.isEmpty( key ) ) {
                this.outputLine("Missing parameter : key");
            } else {
                try (ClientRedisHelper client = this.getHelper()) {
                    String value = client.get( key );
                    if ( value == null ) {
                        this.outputLine("Key '"+key+"' not found");
                    } else {
                        String line = VALUE_FOR_KEY_LIT+key+"' is '"+value+"'";
                        long ttl = client.getTTL(key);
                        if ( ttl >= 0 ) {
                            line+= ", ttl="+(ttl)+"(s)";
                        } else if ( ttl == -1 ) {
                            line+= ", with no expiration";
                        }
                        this.outputLine( line );
                    }
                }
            }
        }, this.getExceptionConsumer( ERROR_GETTING_VALUE_FOR_JEY_LIT+key ) );
    }

    public void del() {
        this.executeStart( "del" );
        String key = this.getKey();
        this.executeStart( "list keys" );
        SafeFunction.apply( () -> {
            try ( ClientRedisHelper client = this.getHelper() ) {
                if ( StringUtils.isEmpty( key ) ) {
                    this.outputLine("Missing parameter : key");
                } else {
                    long value = client.del( key );
                    this.outputLine("Key '"+key+"' del result : "+value );
                }
            }
        }, this.getExceptionConsumer( ERROR_GETTING_VALUE_FOR_JEY_LIT+key ) );
    }

    public void listKeys() {
        this.executeStart( "list keys" );
        SafeFunction.apply( () -> {
            try ( ClientRedisHelper client = this.getHelper() ) {
                for ( String key : client.listKeys() ) {
                    this.outputLine(key);
                }
            }
        }, this.getExceptionConsumer( "Error getting key list" ) );
    }

    public void listAll() {
        this.executeStart( "list all" );
        SafeFunction.apply( () -> {
            try ( ClientRedisHelper client = this.getHelper() ) {
                for ( Map.Entry<String, String> entry : client.all() ) {
                    this.outputLine( entry.getKey()+" : '"+entry.getValue()+"'" );
                }
            }
        }, this.getExceptionConsumer( "Error getting key/value list" ) );
    }

    public void info() {
        this.executeStart( "server info" );
        SafeFunction.apply( () -> {
            try ( ClientRedisHelper client = this.getHelper() ) {
                this.outputLine( client.serverInfo() );
            }
        }, this.getExceptionConsumer( "Error getting server info" ) );
    }

}
