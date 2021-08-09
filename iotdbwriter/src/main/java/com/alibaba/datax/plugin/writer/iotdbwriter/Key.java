package com.alibaba.datax.plugin.writer.iotdbwriter;

import com.alibaba.datax.common.util.Configuration;

public class Key {
    public static String getHost(Configuration conf) {
        return conf.getNecessaryValue(Constant.HOST, IotdbWriterErrorCode.BAD_CONFIG_VALUE);
    }
    public static int getPort(Configuration conf) {
        return conf.getInt(Constant.PORT,6667);
    }
    public static String getUsername(Configuration conf) {
        return conf.getNecessaryValue(Constant.USERNAME, IotdbWriterErrorCode.BAD_CONFIG_VALUE);
    }
    public static String getPassword(Configuration conf) {
        return conf.getNecessaryValue(Constant.PASSWORD, IotdbWriterErrorCode.BAD_CONFIG_VALUE);
    }
    public static int getBatchSize(Configuration conf) {
        return conf.getInt(Constant.BATCHSIZE,5000);
    }
    public static int getTrySize(Configuration conf) {
        return conf.getInt(Constant.TRYSIZE,30);
    }
    public static boolean getEnableRPCCompression(Configuration conf) {
        return conf.getBool(Constant.ENABLE_RPC_COMPRESSION,false);
    }
    public static String getStorageGroup(Configuration conf) {
        return conf.getNecessaryValue(Constant.STORAGEGROUP, IotdbWriterErrorCode.BAD_CONFIG_VALUE);
    }
    public static String getTSDataType(Configuration conf) {
        return conf.getString(Constant.STORAGEGROUP);
    }
}
