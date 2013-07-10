/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobilemouse;

import java.net.*;
import com.sun.net.httpserver.*;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MobileMouse implements HttpHandler {

    private static HttpServer Server;
    private static Robot rob;

    private static void X(String p, Object... x) {
        System.out.println(String.format(p, x));
    }

    public static void main(String[] args) throws Exception {
        rob = new Robot();
        Server = HttpServer.create(new InetSocketAddress("192.168.1.8", 8080), 5);
        Server.createContext("/", new MobileMouse());
        Server.start();
    }

    private static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    }

    @Override
    public void handle(HttpExchange xchg) throws IOException {
        String method = xchg.getRequestMethod();
        String response = "";

        if (method.equals("POST")) {
            InputStream is = xchg.getRequestBody();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while (br.ready()) {
                String s = br.readLine();
                String[] parts = s.split("&");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int click = Integer.parseInt(parts[2]);
                X("Move mouse: %d, %d. Click: %d", x, y, click);
                rob.mouseMove(x, y);
                if(click == 1){
                    rob.mousePress(InputEvent.BUTTON1_MASK);
                }
                else if(click == 2){
                    rob.mousePress(InputEvent.BUTTON2_MASK);
                }
                else if(click == 3){
                    rob.mousePress(InputEvent.BUTTON3_MASK);
                }
            }
        } else if (method.equals("GET")) {
            Headers head = xchg.getResponseHeaders();
            head.set("Content-type", "text/html");

            response = readFile("client.html");
        } else {
            X("METHOD: " + method);
        }
        
        xchg.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
            OutputStream os = xchg.getResponseBody();
            os.write(response.getBytes());
            os.close();
    }
}