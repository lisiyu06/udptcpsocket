package tcp;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Author: lisiyu
 * Created: 2020/2/12
 */
public class TcpServer {

    // 定义一个常量端口号
    private static final int PORT = 9999;

    /**
     * 原生线程池创建方式：
     * 参数1：核心线程数（正式工，启动线程池就运行这个数量的线程）
     * 参数2：最大线程数（正式工+临时工）
     * 参数3+4：一定数量的时间+时间单位，在时间内，临时工的线程没有任务处理，就把临时工解雇掉（关闭线程）
     * 参数5：无边界的工作队列
     * 参数6：代表任务数量超出最大值，线程池应该怎么做（4种策略，现在简单了解）
     */
//    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
//            0, Integer.MAX_VALUE, 30, TimeUnit.SECONDS,
//            new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy()
//    );

    // 线程池中使用的线程，在参数3+4的时间范围内，是可以重用
    // 有新任务进来需要处理，此时有正式工线程空闲，就让正式工处理
    // 如果正式工都没有空闲，让临时工处理（依赖具体是哪种线程池的实现）
    // 就创建新的线程处理（是否创建需要依赖具体是哪种线程池的实现）（新的线程加入正式工或者临时工）

    // 单个线程的线程池：只有一个正式工
//    private static final ExecutorService EXE = Executors.newSingleThreadExecutor();
    // 可缓存的线程：正式工编制为0，所有线程都是临时工
    private static final ExecutorService EXE = Executors.newCachedThreadPool();
    // 定时任务的线程池
//    private static final ExecutorService EXE = Executors.newScheduledThreadPool(1);
    // 固定大小的线程池：只有固定数量编制的正式工
//    private static final ExecutorService EXE = Executors.newFixedThreadPool(10);


    public static void main(String[] args) throws IOException {
        // 启动 TCP 服务器
        ServerSocket serverSocket = new ServerSocket(PORT);

        // 多线程解决多个客户端连接问题，不会造成服务器阻塞
        // 1. 哪些代码应该放在多线程 run() 内
        // 2. 多线程的代码应该放在哪个位置
        // 3. 需要使用 CachedThreadPool 这种类型：
              // 如果使用固定大小的线程池，在达到线程池数量的客户端链接以后，新的客户端就会阻塞

        // 循环获取新的客户端连接
        while (true) {
            // 阻塞，等待新的客户端连接
            Socket socket = serverSocket.accept();
            EXE.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 处理这个客户端连接的业务，这个业务可能发生阻塞
                        // 先不考虑阻塞的实现
                        InputStream is = socket.getInputStream(); // 获取到的是 socket 帮我们包装的一个输入字节流
                        // 缓冲字符流BufferedReader / BufferWriter：字节流要转换为字符流，需要通过
                        // InputStreamReader / OutputStreamWriter：字节字符转换流来进行转换
                        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                        OutputStream os = socket.getOutputStream(); // 获取到的是 socket 帮我们包装的一个输出字节流
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                        // 1. 先接收客户端传过来的数据，并打印
                        // 2. 响应给客户一个数据，我已经接收到了××××消息

                        String line;
                        // 阻塞等待客户端传过来的新的一行数据：readLine 是读取到换行符之前的部分
                        // 接收到 line + \n ，通过 readLine 返回字符串就是 line
                        while ((line = br.readLine()) != null) { // 需要 io 流关闭，或者客户端方法返回(即io 流关闭)
                            System.out.println("服务端接收到数据：" + line);
                            // write 操作指的是将数据写入缓冲区
                            // bufferedWriter 可以改造为 PrintWriter
                            // （1 自动刷新；2 printLine 可以不用手动输入换行符）
                            // TODO (改造)
                            bw.write("我已经接收到了" + line + "消息\n");
                            // bw.write("...");
                            // 需要刷新一下缓冲区, 这时才会将数据发送到对端
                            bw.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }






    }

}
