package com.nxin.framework.etl.designer.enums;

import java.util.Arrays;
import java.util.List;

public interface Constant {
	String ACTIVE = "1";
	String INACTIVE = "0";
	String LOCKED = "2";
	String SCOPE_ALL = "0";
	String SCOPE_PROJECT = "1";
	String DEFAULT_PWD = "123456";
	String JOB = "1";
	String TRANSFORM = "2";
	String ENCODING_UTF_8 = "UTF-8";
	String PASSWORD_ENCRYPTED_PREFIX = "Encrypted ";
	String AUTHORITY_PROBATION = "PROBATION";
	String ETL_START_NAME = "Start";
	String ETL_PARALLEL = "parallel";
	String NUMBER = "Number";
	String BOOLEAN = "Boolean";
	String SHELL_MODE_DESIGN = "DESIGN";
	String SHELL_MODE_SAVE = "SAVE";
	String SHELL_MODE_RUN = "RUN";
	String STREAMING = "1";
	String BATCH = "0";
	String EVN_PROD = "prod";
	String EVN_DESIGNER = "designer";
	List<String> STREAMING_STEP = Arrays.asList("KafkaConsumerInput", "RecordsFromStream");
	int PROBATION_DAYS = 3;
	int EXCEPTION_NOT_FOUNT = 10001;
	int EXCEPTION_UNAUTHORIZED = 10002;
	int EXCEPTION_USERNAME_EXISTED = 10003;
	int EXCEPTION_DISABLED = 10004;
	int EXCEPTION_BAD_CREDENTIALS = 10005;
	int EXCEPTION_EMAIL_EXISTED = 10006;
	int EXCEPTION_DATASOURCE_CONNECT = 10007;
	int EXCEPTION_SQL_GRAMMAR = 10008;
	int EXCEPTION_ETL_GRAMMAR = 10009;
	int EXCEPTION_DATA = 10010;
	int EXCEPTION_RECORDS_NOT_MATCH = 10011;
	int EXCEPTION_ADD_SCHEDULE = 10012;
	int EXCEPTION_CONNECTION_FAILURE = 10013;
	int EXCEPTION_REMOVE_SCHEDULE = 10014;
}
