package com.alibaba.datax.plugin.writer.iotdbwriter;

import com.alibaba.datax.common.spi.ErrorCode;
/**
 * Created by pk on 21/8/17.
 */
public enum IotdbWriterErrorCode  implements ErrorCode {
    BAD_CONFIG_VALUE("IotdbWriter-00", "您配置的值不合法."),
    BAD_STORAGE_SET("IotdbWriter-01", "存储组设置出错."),
    BAD_STATEMENT_EXECUTE("IotdbWriter-02", "语句执行出错."),
    BAD_SESSION_CONNECT("IotdbWriter-03", "session连接出错."),
    BAD_SESSION_CLOSE("IotdbWriter-04", "session关闭出错."),
    ;

    private final String code;
    private final String description;

    IotdbWriterErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Description:[%s]. ", this.code,
                this.description);
    }
}
