package segfault.hydrolog;

import segfault.hydrolog.http.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String... args) throws IOException {
        new HttpServer(new InetSocketAddress(System.getenv("http.addr"),
                Integer.parseInt(System.getenv("http.port")))).start();
    }
}
