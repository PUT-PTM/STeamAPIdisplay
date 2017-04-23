package lolAPI;
public class ProtocolImpl implements Protocol {  
   
    byte[] buffer = new byte[1024];  
    int tail = 0;  
      
    public void onReceive(byte b) {  
        if (b=='\n') {  
            onMessage();  
        } else {  
            buffer[tail] = b;  
            tail++;  
        }  
    }  
   
    public void onStreamClosed() {  
        onMessage();  
    }  
      

    private void onMessage() {  
        if (tail!=0) {  
            String message = getMessage(buffer, tail);  
            System.out.println("RECEIVED MESSAGE: " + message);  
              
            if ("HELO".equals(message)) {  
                CommPortSender.send(getMessage("OK"));  
            } else if ("OK".equals(message)) {  
                CommPortSender.send(getMessage("OK ACK"));  
            }  
            tail = 0;  
        }  
    }  
      
    public byte[] getMessage(String message) {  
        return (message+"\n").getBytes();  
    }  
      
    public String getMessage(byte[] buffer, int len) {  
        return new String(buffer, 0, tail);  
    }  
}  