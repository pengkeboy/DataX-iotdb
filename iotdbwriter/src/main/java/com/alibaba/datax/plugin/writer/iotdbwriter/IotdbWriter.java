package com.alibaba.datax.plugin.writer.iotdbwriter;

import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.fastjson.JSONObject;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.rpc.TSStatusCode;
import org.apache.iotdb.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class IotdbWriter extends Writer {


    public static class Job extends Writer.Job {
        private Configuration conf = null;


        @Override
        public void init() {
            this.conf = super.getPluginJobConf();
        }

        @Override
        public void prepare() {
            super.prepare();
        }

        @Override
        public List<Configuration> split(int mandatoryNumber) {
            List<Configuration> configurations = new ArrayList<Configuration>(mandatoryNumber);
            for (int i = 0; i < mandatoryNumber; i++) {
                configurations.add(conf);
            }
            return configurations;
        }

        @Override
        public void destroy() {

        }
    }


    public static class Task extends Writer.Task {

        private static final Logger logger = LoggerFactory.getLogger(Task.class);
        private Configuration conf;
        private String host;
        private int port;
        private String username;
        private String password;

        private Session session;
        private int batchSize;
        private boolean enableRPCCompression;
        private String storageGroup;
        private String tsDataType;
        private int trySize;
        private String splitter;


        @Override
        public void init() {
            this.conf = this.getPluginJobConf();
            this.host = Key.getHost(conf);
            this.port = Key.getPort(conf);
            this.username = Key.getUsername(conf);
            this.password = Key.getPassword(conf);

            this.batchSize = Key.getBatchSize(conf);
            this.trySize = Key.getTrySize(conf);

            this.enableRPCCompression = Key.getEnableRPCCompression(conf);
            this.storageGroup = Key.getStorageGroup(conf);
            this.tsDataType = Key.getTSDataType(conf);
        }

        @Override
        public void prepare() {
            this.session = new Session(host, port, username, password, batchSize);
            try {
                session.open(enableRPCCompression);
                session.setStorageGroup(storageGroup);
            } catch (StatementExecutionException e) {
                if (e.getStatusCode() != TSStatusCode.PATH_ALREADY_EXIST_ERROR.getStatusCode()){
                logger.error("存储组设置出错:",e.toString());
                }
            } catch (IoTDBConnectionException e) {
                throw DataXException.asDataXException(IotdbWriterErrorCode.BAD_SESSION_CONNECT, e);
            }
        }

        @Override
        public void startWrite(RecordReceiver recordReceiver) {
            List<String> deviceIds = new ArrayList(this.batchSize);
            List<Long> timeList = new ArrayList(this.batchSize);
            List<List<String>> measurementsList = new ArrayList(this.batchSize);
            List<List<String>> valuesList = new ArrayList(this.batchSize);
            Record record = null;
            long total = 0;
            while ((record = recordReceiver.getFromReader()) != null) {
                String deviceId = record.getColumn(0).asString();
                deviceIds.add(deviceId);
                long time = record.getColumn(1).asLong();
                timeList.add(time);
                List<String> measurements = JSONObject.parseArray(record.getColumn(2).asString(), String.class);
                measurementsList.add(measurements);
                List<String> values = JSONObject.parseArray(record.getColumn(3).asString(), String.class);
                valuesList.add(values);
                if (deviceIds.size() >= this.batchSize) {
                    doBatchInsert(deviceIds, timeList, measurementsList, valuesList);
                    total += deviceIds.size();
                    doBatchClear(deviceIds, timeList, measurementsList, valuesList);
                }
            }
            if (!deviceIds.isEmpty()) {
                doBatchInsert(deviceIds, timeList, measurementsList, valuesList);
                total += deviceIds.size();
                doBatchClear(deviceIds, timeList, measurementsList, valuesList);
            }
            String msg = String.format("task end, write size :%d", total);
            getTaskPluginCollector().collectMessage("writesize", String.valueOf(total));
            logger.info(msg);
        }

        private void doBatchInsert(List<String> deviceIds, List<Long> timeList, List<List<String>> measurementsList, List<List<String>> valuesList) {
            try {
                session.insertRecords(deviceIds, timeList, measurementsList, valuesList);
            } catch (IoTDBConnectionException e) {
                throw DataXException.asDataXException(IotdbWriterErrorCode.BAD_SESSION_CONNECT, e);
            } catch (StatementExecutionException e) {
                throw DataXException.asDataXException(IotdbWriterErrorCode.BAD_STATEMENT_EXECUTE, e);
            }
        }

        private void doBatchClear(List<String> deviceIds, List<Long> timeList, List<List<String>> measurementsList, List<List<String>> valuesList) {
            deviceIds.clear();
            timeList.clear();
            measurementsList.clear();
            valuesList.clear();
        }


        @Override
        public void destroy() {
            try {
                this.session.close();
            } catch (IoTDBConnectionException e) {
                throw DataXException.asDataXException(IotdbWriterErrorCode.BAD_SESSION_CLOSE, e);
            }
        }

    }

//    public static void main(String[] args) {
//        String test=" {\n" +
//                "      \"device\":\"root.sg.d1\",\n" +
//                "      \"timestamp\":1586076045524,\n" +
//                "      \"measurements\":[\"s1\",\"s2\"],\n" +
//                "      \"values\":[0.530635,0.530635]\n" +
//                " }";
//        JSONObject jsonTest=JSONObject.parseObject(test);
//      // JSONArray jsonArray= jsonTest.getJSONArray("values").toJSONString();
////       for(Object obj:jsonArray){
////           System.out.println(obj.toString());
////       }
//   List<String> list = JSONObject.parseArray(jsonTest.getJSONArray("values").toJSONString(), String.class);
//        System.out.println(list);
//    }
}
