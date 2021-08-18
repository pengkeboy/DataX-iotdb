# DataX iotdbWriter


---

## 1 快速介绍

数据导入iotdb的插件

## 2 实现原理

使用Java原生接口， 批量把从reader读入的数据写入iotdb

## 3 功能说明

### 3.1 配置样例

#### job.json

```
{
	"job": {
		"setting": {
			"speed": {
				"channel": 1
			}
		},
		"content": [{
			"reader": {
				...
			},
			"writer": {
				"name": "iotdbwriter",
				"parameter": {
					"host": "127.0.0.1",
					"port": 6667,
					"username": "root",
					"password": "root",
					"storageGroup": "root.ln",
                    "batchSize": 5000
				}
			}
		}]
	}
}
```

#### 3.2 参数说明（各个配置项前后不允许有空格）

* host
 * 描述：iotdb的连接地址
 * 必选：是
 * 默认值：无

* port
 * 描述：iotdb的rpc port
 * 必选：否
 * 默认值：6667

* username
 * 描述：iotdb连接用户名
 * 必选：是
 * 默认值：空

* password
 * 描述：iotdb连接密码
 * 必选：是
 * 默认值：无

* storageGroup
 * 描述：iotdb存储组
 * 必选：是
 * 默认值：无

* batchSize
 * 描述：写入批次大小
 * 必选：否
 * 默认值：5000

## 4 性能报告

### 4.1 环境准备


#### 4.1.1 输入数据类型(streamreader)

```
```

#### 4.1.2 输出数据类型(iotdbwriter)

```
```

#### 4.1.2 机器参数


#### 4.1.3 DataX jvm 参数

-Xms1g -Xmx1g -XX:+HeapDumpOnOutOfMemoryError

### 4.2 测试报告


### 4.3 测试总结


## 5 约束限制

* deviceid,time,measurements,values 严格有序，示例值：root.ln.wf01.wt01,1628490506000,["s1","s2"],[0.530635,0.530635]
