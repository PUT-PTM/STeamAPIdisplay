package lolAPI;
import gnu.io.CommPortIdentifier;  
import gnu.io.SerialPort;  
   
public class JavaMicrocontrollerCommunicator {  
     
    public void connect(String portName) throws Exception {  
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);  
   
        if (portIdentifier.isCurrentlyOwned()) {  
            System.out.println("Port in use!");  
        } else {  
            // points who owns the port and connection timeout  
            SerialPort serialPort = (SerialPort) portIdentifier.open("JavaMicrocontrollerCommunicator", 2000);  
              
            // setup connection parameters  
            serialPort.setSerialPortParams(  
                38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);  
   
            // setup serial port writer  
            CommPortSender.setWriterStream(serialPort.getOutputStream());  
              

        }  
    }  
      
    public static void main(String[] args) throws Exception {  
          
        // connects to the port which name (e.g. COM1) is in the first argument  
        new JavaMicrocontrollerCommunicator().connect("COM5");  
          
        // send HELO message through serial port using protocol implementation  
         CommPortSender.send(new ProtocolImpl().getMessage("tekst"));  
         CommPortSender.send(new ProtocolImpl().getMessage("tekst"));  
         CommPortSender.send(new ProtocolImpl().getMessage("tekst"));  


    }  
}  