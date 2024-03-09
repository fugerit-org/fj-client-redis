package org.fugerit.java.client.redis;

public class ClientRedisArgs {

	private ClientRedisArgs() {}
	
	public static final String ARG_MODE = "mode";
	public static final String MODE_SINGLE_COMMAND = "single-command";
	public static final String MODE_GUI = "gui";
	public static final String MODE_DEFAULT = MODE_SINGLE_COMMAND;
	
	public static final String ARG_REDIS_URL = "redis-url";
	public static final String ARG_KEY = "key";
	public static final String ARG_VALUE = "value";
	public static final String ARG_TTL = "ttl";

	public static final String ARG_LIST = "list";

	public static final String ARG_LIST_KEYS = "keys";

	public static final String ARG_LIST_ALL = "all";
	
}

