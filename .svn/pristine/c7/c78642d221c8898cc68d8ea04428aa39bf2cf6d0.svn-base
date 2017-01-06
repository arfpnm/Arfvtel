package	com.telappliant.tvoip.asterisk;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class ManagerInterface
{
	private static  Logger log = Logger.getLogger(ManagerInterface.class.getName());

	protected	String		name;
	protected	String		addr;
	protected	int		port;
	protected	String		user;
	protected	String		pass;
	protected	boolean		tls;

	protected	Socket		socket;

	protected	BufferedReader	reader;
	protected	BufferedWriter	writer;

	protected	String		openError;

	public ManagerInterface(
			String	name,
			String	addr,
			int	port,
			String	user,
			String	pass,
			boolean	tls)
	{
		this.name = name;
		this.addr = addr;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.tls = tls;

		openError = null;
	}

	public String getName()
	{
		return name;
	}

	public String getAddress()
	{
		return addr;
	}

	public int getPort()
	{
		return port;
	}

	public boolean open(
			boolean events)
	{
		try
		{
			openError = null;

			socket = (tls ? SSLSocketFactory.getDefault() : SocketFactory.getDefault()).createSocket(addr, port);

			socket.setKeepAlive(true);

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			boolean	ok = doSimpleCmd("login",
					"Username", user,
					"Secret", pass,
					"Events", events ? "on" : "off");

			if (ok == false)
			{
				openError = "Login Failed !!";
				log.info("ManagerInterface Can't Login " + addr + ":" + port);
				close(false);
			}

			return ok;
		}
		catch (Exception e)
		{
			openError = e.getMessage();

			log.info("ManagerInterface Can't Open " + addr + ":" + port + " :: " + openError);

			close();

			return false;
		}
	}

	public String getOpenError()
	{
		return openError;
	}

	public void close()
	{
		close(true);
	}

	protected void close(
			boolean	logOff)
	{
		try
		{
			if (writer != null)
			{
				if (logOff)
					simpleCmd("Logoff");

				writer.close();
				writer = null;
			}

			// Give logoff time to reply
			Thread.sleep(1000);

			if (reader != null)
			{
				reader.close();
				reader = null;
			}

			if (socket != null)
			{
				socket.close();
				socket = null;
			}
		}
		catch (Exception e)
		{
			log.info("ManagerInterface Can't Close " + addr + ":" + port + " :: " + e.getMessage());
		}
	}

	public boolean isOpen()
	{
		return writer != null;
	}

	public boolean isResponseSuccess(
			Event	reply)
	{
		return reply != null && "Success".equalsIgnoreCase(reply.get("Response"));
	}

	public synchronized void write(
			String	cmd)	throws IOException
	{
		writer.write(cmd);
		writer.flush();
	}

	public void sendCmd(

			String		cmd,
			String...	args)	throws IOException
	{
		StringBuilder	sb = new StringBuilder();
		sb.append("Action: ").append(cmd).append("\r\n");

		for (int i = 0 ; i < args.length ; i += 2)
			sb.append(args[i]).append(": ").append(args[i + 1]).append("\r\n");

		sb.append("\r\n");

		log.info("Cmd :; " + name + " :: " + sb);

		write(sb.toString());
	}

	public boolean doSimpleCmd(
			String		cmd,
			String...	args)	throws IOException
	{
		return isResponseSuccess(simpleCmd(cmd, args));
	}

	public Event simpleCmd(
			String		cmd,
			String...	args)	throws IOException
	{
		sendCmd(cmd, args);

		return readReply();
	}

	public Event getChannel(
			String	channel)	throws IOException
	{
		List<Event>	reply = eventCmd("Status", "Channel", channel);

		for (Event c : reply)
			if (channel.equals(c.get("Channel")))
				return c;

		return null;
	}

	public List<Event> eventCmd(
			String		cmd,
			String...	args)	throws IOException
	{
		String	endEvent = cmd + "Complete";
		String	event;

		sendCmd(cmd, args);

		List<Event>	list = new ArrayList<Event>();
		Event		reply = readReply();

		if (isResponseSuccess(reply))
		{
			while ((reply = readReply()) != null &&
					cmd.equalsIgnoreCase(reply.get("Event")))
			{
				list.add(reply);
			}
		}

		return list;
	}

	public Event readReply() throws IOException
	{
		String	line;

		Event	reply = new Event(name);

		while ((line = reader.readLine()) != null && line.length() > 0)
		{
			int	split = line.indexOf(':');

			if (split == -1)
				reply.put(line.trim(), line.trim());
			else
			{
				String	k = line.substring(0, split).trim();
				String	v = line.substring(split + 1).trim();

				if ((split = v.indexOf('=')) > -1)
				{
					k = k + ':' + v.substring(0, split);
					v = v.substring(split + 1);
				}

				reply.put(k, v);
			}
		}

		if (reply != null)
			log.finer(reply.toString());

		return line != null ? reply : null;
	}

	public void playDtmf(
			String	ca_cid,
			String	channel,
			String	dtmf)
					throws IOException
	{
		if (channel != null)
		{
			for (Character digit : dtmf.toCharArray())
			{
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException ie) { }

				sendCmd("PlayDTMF", "ActionID", "DTMF-" + ca_cid + "-" + digit, "Channel", channel, "Digit", digit.toString());
			}
		}
	}

}
