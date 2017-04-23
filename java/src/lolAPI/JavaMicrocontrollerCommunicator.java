package lolAPI;
import gnu.io.CommPortIdentifier;  
import gnu.io.SerialPort;  
   
public class JavaMicrocontrollerCommunicator {  
     
    public void connect(String portName) throws Exception {  
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);  
   
        if (portIdentifier.isCurrentlyOwned()) {  
            System.out.println("Port in use!");  
        } else {  
            SerialPort serialPort = (SerialPort) portIdentifier.open("JavaMicrocontrollerCommunicator", 2000);  
              
            serialPort.setSerialPortParams(  
                38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);  
   
            CommPortSender.setWriterStream(serialPort.getOutputStream());  
              

        }  
    }  
      
    public static void main(String[] args) throws Exception {  
          
        new JavaMicrocontrollerCommunicator().connect("COM5");  
          
         CommPortSender.send(new ProtocolImpl().getMessage("tekst"));  



    }  
}  