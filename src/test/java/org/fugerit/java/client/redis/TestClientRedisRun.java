package org.fugerit.java.client.redis;


import lombok.extern.slf4j.Slf4j;
import org.fugerit.java.core.cli.ArgUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Testcontainers
@Slf4j
class TestClientRedisRun {

    private final static String KEY_TEST_1 = "key-test-1";

    private final static String KEY_TEST_2 = "key-test-2";

    @Container
    GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:7.2-alpine")).withExposedPorts(6379);

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
        } catch (Exception e) {
            log.warn( "Errore in unit test : "+e ,e );
            ok = false;
        }
        Assert.assertTrue( ok );
    }

    @Test
    void testModeNotSupported() {
        boolean ok = true;
        try {
            // modeUnkown
            String[] modeUnkown = getParameters( ArgUtils.getArgString( ClientRedisArgs.ARG_MODE ), "unknown" );
            ClientRedisRun.main( modeUnkown );
        } catch (Exception e) {
            log.warn( "Errore in unit test : "+e ,e );
            ok = false;
        }
        Assert.assertTrue( ok );
    }

    @Test
    void testWrongConfiguration() {
        boolean ok = true;
        try {
            // modeUnkown
            String[] wrongConfiguration = getParameters( ArgUtils.getArgString( ClientRedisArgs.ARG_MODE ), ClientRedisArgs.MODE_SINGLE_COMMAND );
            ClientRedisRun.main( wrongConfiguration );
        } catch (Exception e) {
            log.warn( "Errore in unit test : "+e ,e );
            ok = false;
        }
        Assert.assertTrue( ok );
    }


}
