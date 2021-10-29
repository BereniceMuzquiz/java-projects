

/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.clientsocket;


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;




/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 */
public class ClientSocketInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opOutA = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutB = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpConnected = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpDisconnected = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propHostname = "localhost";
	int propPort = 1111;
	int propProtocol = 0;

	// declare member variables here
	private Socket clientSocket;
	private BufferedWriter socketOut;
	private BufferedReader socketIn;

	ExecutorService clientReader = Executors.newSingleThreadExecutor();

	private String inputLine; // input string ("inA")

    
   /**
    * The class constructor.
    */
    public ClientSocketInstance()
    {
        // empty constructor
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("inA".equalsIgnoreCase(portID))
		{
			return ipInA;
		}
		if ("inB".equalsIgnoreCase(portID))
		{
			return ipInB;
		}

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("outA".equalsIgnoreCase(portID))
		{
			return opOutA;
		}
		if ("outB".equalsIgnoreCase(portID))
		{
			return opOutB;
		}

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("connect".equalsIgnoreCase(eventPortID))
		{
			return elpConnect;
		}
		if ("disconnect".equalsIgnoreCase(eventPortID))
		{
			return elpDisconnect;
		}
		if ("reconnect".equalsIgnoreCase(eventPortID))
		{
			return elpReconnect;
		}

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if ("connected".equalsIgnoreCase(eventPortID))
		{
			return etpConnected;
		}
		if ("disconnected".equalsIgnoreCase(eventPortID))
		{
			return etpDisconnected;
		}

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("hostname".equalsIgnoreCase(propertyName))
		{
			return propHostname;
		}
		if ("port".equalsIgnoreCase(propertyName))
		{
			return propPort;
		}
		if ("protocol".equalsIgnoreCase(propertyName))
		{
			return propProtocol;
		}

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if ("hostname".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propHostname;
			propHostname = (String)newValue;
			return oldValue;
		}
		if ("port".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPort;
			propPort = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("protocol".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propProtocol;
			propProtocol = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInA  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			inputLine = ConversionUtils.stringFromBytes(data);
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipInB  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpConnect = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here
				 connect(); 
		}
	};
	final IRuntimeEventListenerPort elpDisconnect = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
				 shutdown();
		}
	};
	final IRuntimeEventListenerPort elpReconnect = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here	 
				 shutdown(); 
				 connect();
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
		  //Connect to server socket
		  connect();

          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
		  //Stop connection to server
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
		  //Connect to server socket
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
		  //Stop connection to server

          super.stop();
		  shutdown();
	  }
	  
	  public void connect() {
		  // Connect to server socket in a new thread
			try {
				clientSocket = new Socket(propHostname, propPort);
				socketOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				ExecutorService clientReader = Executors.newSingleThreadExecutor();

				clientReader.execute(new Runnable() {
					public void run() {
						// connect

						String line;
						try {
						//read line from inA which is saved in variable inputLine and send it to the server
							while (!Thread.interrupted() && (inputLine != null)) {
								socketOut.write(inputLine);
								socketOut.flush();
								socketOut.close();
							}
						// read line from socket and send it to output port outA which is represented by
						// the variable opOutA.
							while (!Thread.interrupted() && (line = socketIn.readLine()) != null) {
								opOutA.sendData(ConversionUtils.stringToBytes(line));
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				shutdown();
			}
			//trigger event connected
			etpConnected.raiseEvent();
	 }

	 private void shutdown() {
		clientReader.shutdownNow();
		try {
			clientReader.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
		}

		if (clientSocket != null) {
			try {
				clientSocket.close();
			} catch (Exception e) {
			}
			clientSocket = null;
			socketIn = null;
			socketOut = null;
		}
		//trigger event disconnected
		etpDisconnected.raiseEvent();
	}
}
