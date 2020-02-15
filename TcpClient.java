package tcp;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Author: lisiyu
 * Created: 2020/2/12
 */
public class TcpClient {

    // private static final String HOST = "192.168.1.4";
    // private static final String HOST = "127.0.0.1";
    // 本机默认的域名就是 localhost ，默认 ip 就是127.0.0.1
    // localhost 会通过本机 C://windows/system32/drivers/etc/hosts 文件(本机 DNS 缓存) 转换为本机 ip 地址 127.0.0.1
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    public static void main(String[] args) throws IOException {
        // 建立了客户端到服务端的一个 TCP 连接
        Socket socket = new Socket(HOST, PORT);

        // 处理这个客户端连接的业务，这个业务可能发生阻塞
        // 先不考虑阻塞的实现
        InputStream is = socket.getInputStream(); // 获取到的是 socket 帮我们包装的一个输入字节流
        // 缓冲字符流BufferedReader / BufferWriter：字节流要转换为字符流，需要通过
        // InputStreamReader / OutputStreamWriter：字节字符转换流来进行转换
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        OutputStream os = socket.getOutputStream(); // 获取到的是 socket 帮我们包装的一个输出字节流
        PrintWriter pw = new PrintWriter(os, true);
//        pw.println("hello, 我来了\n");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine(); // 已经去除了换行符
            // 发送数据报到服务端
            pw.println(line);  // println 发送的数据报会加上换行符：发送 line + \n
            // 接收服务端的响应信息
            String response = br.readLine();
            System.out.println("接收到服务端的响应" + response);
        }
    }
}
