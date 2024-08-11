package org.fugerit.java.client.redis;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.core.lang.ex.CodeRuntimeException;
import org.fugerit.java.core.lang.helpers.BooleanUtils;
import org.fugerit.java.tool.util.MainHelper;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;

@Testcontainers
@Slf4j
class TestClientRedisRun {

    protected static final String KEY_TEST_1 = "key-test-1";

    protected static final String KEY_TEST_2 = "key-test-2";

    @Container
    GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:7.2-alpine")).withExposedPorts(6379);

    private IntConsumer originalExitAction;

    @BeforeEach
    void preSetup() {
        this.originalExitAction = MainHelper.getDefaultExitAction();
        MainHelper.setDefaultExitAction( ec -> {
            if ( ec == MainHelper.OK_DEFAULT ) {
                log.info( "exit ok!" );
            } else {
                throw new CodeRuntimeException( ec );
            }
        } );
    }

    @AfterEach
    void postSetup() {
        MainHelper.setDefaultExitAction( this.originalExitAction );
    }

    private String[] getParameters( String... additionalParams ) {

        String address = redis.getHost();
        Integer port = redis.getFirstMappedPort();
        String redisUrl = "redis://"+address+":"+port;
        log.info( "set redis url : {}", redisUrl );
        List<String> params = new ArrayList<>( Arrays.asList( ArgUtils.getArgString( ClientRedisArgs.ARG_REDIS_URL ), redisUrl ) );
        params.addAll( Arrays.asList( additionalParams ) );
        return params.toArray( new String[0] );
    }

    @Test
    void allTest() {
        boolean ok = true;
        try {
            // set
            String[] argsSet = getParameters( ArgUtils.getArgString( ClientRedisArgs.ARG_KEY ), KEY_TEST_1,
                    ArgUtils.getArgString( ClientRedisArgs.ARG_VALUE ), new Date( System.currentTimeMillis() ).toString() );
            ClientRedisRun.main( argsSet );
            // set with ttl
            String[] argsSetWithTtl = getParameters( ArgUtils.getArgString( ClientRedisArgs.ARG_TTL ), "3600",
                    ArgUtils.getArgString( ClientRedisArgs.ARG_KEY ), KEY_TEST_2,
                    ArgUtils.getArgString( ClientRedisArgs.ARG_VALUE ), new Date( System.currentTimeMillis() ).toString() );
            ClientRedisRun.main( argsSetWithTtl );
            // get
            String[] argsGet = getParameters( ArgUtils.getArgString( ClientRedisArgs.ARG_KEY ), KEY_TEST_1 );
            ClientRedisRun.main( argsGet );
            // list keus
            String[] argsListKeys = getParameters( ArgUtils.getArgString( ClientRedisArgs.ARG_LIST ), ClientRedisArgs.ARG_LIST_KEYS  );
            ClientRedisRun.main( argsListKeys );
            // list all
            String[] argsListAll = getParameters( ArgUtils.getArgString( ClientRedisArgs.ARG_LIST ), ClientRedisArgs.ARG_LIST_ALL  );
            ClientRedisRun.main( argsListAll );
            // info
            String[] argsInfo = getParameters( ArgUtils.getArgString( ClientRedisArgs.ARG_INFO ), BooleanUtils.BOOLEAN_1);
            ClientRedisRun.main( argsInfo );
        } catch (Exception e) {
            log.warn( "Errore in unit test : "+e ,e );
            ok = false;
        }
        Assert.assertTrue( ok );
    }

    @Test
    void testModeNotSupported() {
        try {
            // modeUnkown
            String[] modeUnkown = getParameters( ArgUtils.getArgString( ClientRedisArgs.ARG_MODE ), "unknown" );
            ClientRedisRun.main( modeUnkown );
        } catch (CodeRuntimeException e) {
            log.info( "Exit code : {}", e.getCode() );
            Assertions.assertEquals( MainHelper.FAIL_MISSING_REQUIRED_PARAM, e.getCode() );
        }
    }

    @Test
    void testWrongConfiguration() {
        try {
            String[] wrongConfiguration = getParameters(ArgUtils.getArgString(ClientRedisArgs.ARG_MODE), ClientRedisArgs.MODE_SINGLE_COMMAND);
            ClientRedisRun.main(wrongConfiguration);
        } catch ( CodeRuntimeException e ) {
            log.info( "Exit code : {}", e.getCode() );
            Assertions.assertEquals( MainHelper.FAIL_MISSING_REQUIRED_PARAM, e.getCode() );
        }
    }

    @Test
    void testGuiFun() {
        try {
            String address = redis.getHost();
            Integer port = redis.getFirstMappedPort();
            String redisUrl = "redis://"+address+":"+port;
            ClientRedisGUIFunTester fun = new ClientRedisGUIFunTester( redisUrl );
            fun.get();  // not found
            Assertions.assertEquals( KEY_TEST_1, fun.getKey() );
            fun.set();
            fun.get();  // found
            fun.del();
            fun.listKeys();
            fun.listAll();
            fun.info();
            fun.setKey( null );
            fun.get();  // null key
            fun.set();
            fun.del();
            fun.error( "Test error", new ConfigException( "Test exception" ) );
        } catch ( CodeRuntimeException e ) {
            log.info( "Exit code : {}", e.getCode() );
            Assertions.assertEquals( MainHelper.FAIL_MISSING_REQUIRED_PARAM, e.getCode() );
        }
    }

}

@Slf4j
class ClientRedisGUIFunTester extends ClientRedisFun {

    @Setter private String key = TestClientRedisRun.KEY_TEST_1;
    @Setter private String value = "test-value-1";
    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getTTL() {
        return "100";
    }

    @Override
    public void outputLine(String line) {
        log.info( "outputLine : {}", line );
    }

    ClientRedisGUIFunTester( String redisUrl ) {
        super(redisUrl);
    }

}