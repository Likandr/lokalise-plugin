package com.likandr.gradle

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * @author www.codejava.net
 *
 */
class MultipartUtility {
    private final String boundary
    private static final String LINE_FEED = "\r\n"
    private HttpURLConnection httpConn
    private String charset
    private OutputStream outputStream
    private PrintWriter writer

    MultipartUtility(String requestURL, String charset) throws IOException {
        this.charset = charset

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "==="

        URL url = new URL(requestURL)
        httpConn = (HttpURLConnection) url.openConnection()
        httpConn.setDoOutput(true) // indicates POST method
        httpConn.setDoInput(true)
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary)
        outputStream = httpConn.getOutputStream()
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true)
    }

    void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED)
        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.append(value).append(LINE_FEED)
        writer.flush()
    }

    void addFilePart(String fieldName, File uploadFile) throws IOException {
        String fileName = uploadFile.getName()
        writer.append("--" + boundary).append(LINE_FEED)
        writer.append( "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED)
        writer.append( "Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED)
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.flush()

        FileInputStream inputStream = new FileInputStream(uploadFile)
        byte[] buffer = new byte[4096]
        int bytesRead = -1
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.flush()
        inputStream.close()

        writer.append(LINE_FEED)
        writer.flush()
    }

    List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>()

        writer.append(LINE_FEED).flush()
        writer.append("--" + boundary + "--").append(LINE_FEED)
        writer.close()

        // checks server's status code first
        int status = httpConn.getResponseCode()
        println status
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader( httpConn.getInputStream()))
            String line = null
            while ((line = reader.readLine()) != null) {
                response.add(line)
            }
            reader.close()
            httpConn.disconnect()
        } else {
            throw new IOException("Server returned non-OK status: " + status)
        }
        return response
    }
}
