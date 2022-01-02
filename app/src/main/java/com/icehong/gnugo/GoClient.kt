package com.icehong.gnugo

import android.util.Log
import java.io.*
import java.lang.Thread.sleep
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.concurrent.SynchronousQueue
import kotlin.concurrent.thread

class GoClient (private val ip: String, private val port: Int){
    //普通数据交互接口
    private var sc: Socket? = null

    //普通交互流
    private var dout: OutputStream? = null
    private var din: InputStreamReader? = null

    //已连接标记
    private val isConnect get() = sc != null && din != null && dout != null

    private val queue = SynchronousQueue<String>(true)

    /**
     * 初始化普通交互连接
     */
    fun initConnect() {
        var i = 0 ;
        while (!isConnect && i < 3){
            try {
                sc = Socket(ip, port) //通过socket连接服务器
                din = InputStreamReader(sc?.getInputStream(),"UTF-8")
                dout = sc?.getOutputStream()    //获取输出流
                sc?.soTimeout = 10000  //设置连接超时限制
                if (isConnect) {
                    Log.d("GoClient", "connect server successful")
                    return
                } else {
                    Log.d("GoClient", "connect server failed(${i}), now retry...")
                }
            } catch (e: IOException) {      //获取输入输出流是可能报IOException的，所以必须try-catch
                e.printStackTrace()
            }
            i++
            sleep((1000 * i).toLong())
        }
    }


    fun asynSendMessage(message: String?) {
        queue.put(message)
    }
    fun sendMessage(message: String?) {
         sendMessage(message?.toByteArray())
    }

    fun sendMessage(message: ByteArray?): Boolean {
        try {
            if (isConnect) {
                if (message != null) {        //判断输出流或者消息是否为空，为空的话会产生null pointer错误
                    dout?.write(message)
                    dout?.flush()
                    Log.d("GoClient", "Send msg: ${message.decodeToString()}")
                    return true
                } else Log.d("GoClient", "The message to be sent is empty")
                Log.d("GoClient", "send message succeed")
            } else Log.d("GoClient", "send message failed: no connect")
        } catch (e: IOException) {
            Log.d("GoClient", "send message failed: crash")
        }
        return false
    }

    fun sendThread() {
        val mRunnable = Runnable {
            run {
                while (isConnect) {
                    val msg = queue.take()
                    Log.d("GoClient", "got message ${msg}")
                    sendMessage(msg)
                }
            }
        }
        Thread(mRunnable).start()
    }

    fun receiveThread(){
        val mRunnable = Runnable {
            run {
                while (isConnect) {
                    try {
                        val inMessage = CharArray(1024)
                        val a = din?.read(inMessage) //a存储返回消息的长度
                        if (a == null || a <= -1) {	//接受到的消息没有长度，即代表服务端发送了空的消息
                            sleep(1000)
                            Log.d("GoClient", "no message, delay 1000ms.")
                            continue
                        }
                        var recvMsg = String(inMessage, 0, a) //用string的构造方法来转换字符数组为字符串
                        Log.d("GoClient", "receive: $recvMsg")
                    } catch (e: SocketTimeoutException) {
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        Thread(mRunnable).start()
        Log.d("GoClient", "receive thread started ")
    }

    /**
     * 关闭连接
     */
    fun closeConnect() = try {
        din?.close()
        dout?.close()
        sc?.close()
        sc = null
        din = null
        dout = null
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}