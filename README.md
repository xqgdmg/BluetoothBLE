# BluetoothBLE
原文：https://blog.csdn.net/Fight_0513/article/details/79855749 &lt;br/>&lt;br/>4.x开始的蓝牙我们称之为低功耗蓝牙也就是蓝牙ble，当然4.x版本的蓝牙也是向下兼容的。android手机必须系统版本4.3及以上才支持BLE API。低功耗蓝牙较传统蓝牙，传输速度更快，覆盖范围更广，安全性更高，延迟更短，耗电极低等等优点。（现在的穿戴设备都是使用BLE蓝牙技术的）&lt;br/>BLE的三部分：Service，Characteristic，Descriptor 这三部分都用UUID作为唯一标识符。UUID为这种格式：0000ffe1-0000-1000-8000-00805f9b34fb。比如有3个Service，那么就有三个不同的UUID与Service对应。这些UUID都写在硬件里，我们通过BLE提供的API可以读取到。  一个BLE终端可以包含多个Service， 一个Service可以包含多个Characteristic，一个Characteristic包含一个value和多个Descriptor，一个Descriptor包含一个Value。Characteristic是比较重要的，是手机与BLE终端交换数据的关键，读取设置数据等操作都是操作Characteristic的相关属性。 &lt;br/>
