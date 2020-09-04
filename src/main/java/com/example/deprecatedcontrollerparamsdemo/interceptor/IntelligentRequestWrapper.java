package com.example.deprecatedcontrollerparamsdemo.interceptor;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class IntelligentRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public IntelligentRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return super.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return super.getReader();
    }

    private static class InnerServletInputStream extends ServletInputStream {

        private final InputStreamReader reader;
        private final char[] buffer = new char[1024];
        private int bufferOffset = 0;
        private int bufferLength = 0;

        public InnerServletInputStream(InputStream inputStream) {
            this.reader = new InputStreamReader(inputStream);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }

        @Override
        public int read() throws IOException {
            if (bufferLength > bufferOffset) {
                return buffer[bufferOffset++];
            }
            bufferLength = 0;
            bufferOffset = 0;

            int first = -1;
            int last = -1;

            while (last < 0) {
                int next = reader.read();
                if (next < 0) {
                    return -1;
                }
                buffer[bufferLength++] = (char) next;
                if (next == '"') {
                    if (first < 0) {
                        first = bufferLength;
                    } else {
                        last = bufferLength - 1;
                    }
                }
            }
            String s = new String(buffer, first, last - first);
            if (s.equals("build")) {
                char[] chars = "featureset".toCharArray();
                for (char c : chars) {
                    buffer[first++] = c;
                }
                buffer[first] = '"';
                bufferLength = bufferLength - "build".length() + "featureset".length();
            }
            return buffer[bufferOffset++];
        }
    }

    public static void main(String[] args) throws IOException {
        InnerServletInputStream innerServletInputStream = new InnerServletInputStream(new ByteArrayInputStream("{\"asdasd\":\"zxczxc\",\"build\":\"zxczcxzc\"}".getBytes()));
        System.out.print(new String(innerServletInputStream.readAllBytes()));
    }
}
