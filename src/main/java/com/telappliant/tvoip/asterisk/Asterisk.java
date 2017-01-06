package	com.telappliant.tvoip.asterisk;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author arif.mohammed
 *
 */
public class Asterisk implements Runnable
{
private static  Logger log = Logger.getLogger(Asterisk.class.getName());

protected	ExecutorService	 executor;

protected	ManagerInterface	eventAmi;

protected	ArrayList<Listener>	listeners = new ArrayList<Listener>();

protected	String			type = null;

/**
 * @param executor //Executors.newCachedThreadPool();
 * @param name //gateway VIOP Office
 * @param addr //IP address GateWay VOIP office
 * @param port //port VOIP office
 * @param user //user VOIP office
 * @param pass //password VOIP office
 * @param tls //YES/NO
 */
public Asterisk(
	ExecutorService	executor,
	String		name,
	String		addr,
	int		port,
	String		user,
	String		pass,
	boolean		tls)
	{
	this.executor = executor;

	eventAmi = new ManagerInterface(name, addr, port, user, pass, tls);
	}

public boolean addListener(
	Listener	listener)
	{
	return listeners.add(listener);
	}

public boolean removeListener(
	Listener	listener)
	{
	return listeners.remove(listener);
	}

public ManagerInterface getManager()
	{
	return eventAmi;
	}

public boolean start()
	{
	executor.submit(this);

	return true;
	}

public boolean stop()
	{
	log.info("process stopped");
	close();

	return true;
	}

public boolean open()
	{
	return eventAmi.open(true);
	}

public void close()
	{
	eventAmi.close();
	}

public void run()
	{
	while (!executor.isShutdown())
		{
		try
			{
			if (open())
				{
				try
					{
					for (Event event = eventAmi.readReply() ; event != null ; event = eventAmi.readReply())
						{
						log.info(event.toString());

						if (!executor.isShutdown())
							executor.submit(new ExecEvent(event));
						}
					}
				catch (IOException ioe)
					{
					log.info(ioe.getMessage());
					}

				close();
				}
			else
				Thread.sleep(2000);
			}
		catch (Exception e)
			{
			log.log(Level.SEVERE, "Asterisk Event ::" , e);
			}
		}
	}

public static interface Listener
	{
	public void handleEvent(Asterisk asterisk, Event event) throws IOException;
	public void handleResponse(Asterisk asterisk, Event response) throws IOException;
	}

protected class ExecEvent implements Runnable
	{
	protected	Event	event;

	public ExecEvent(
		Event	event)
		{
		this.event = event;
		}

	public void run()
		{
		for (Listener listener : listeners)
			{
			try
				{
				if (event.isEvent())
					listener.handleEvent(Asterisk.this, event);
				else
					listener.handleResponse(Asterisk.this, event);
				}
			catch (Exception e)
				{
				log.log(Level.SEVERE, "Listener :: " + listener, e);
				}
			}
		}
	}
}
