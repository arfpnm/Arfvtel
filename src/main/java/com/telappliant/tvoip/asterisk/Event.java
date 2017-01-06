package	com.telappliant.tvoip.asterisk;

import java.util.LinkedHashMap;

public class Event extends LinkedHashMap<String, String>
{
public	static	final	String	EVENT = "Event";
public	static	final	String	RESPONSE = "Response";

protected	String	source;

public Event()
	{
	}

public Event(
	String	source)
	{
	this.source = source;
	}

public boolean containsKey(
	String	key)
	{
	return super.containsKey(key.toLowerCase());
	}

public String get(
	String	key)
	{
	return super.get(key.toLowerCase());
	}

public String put(
	String	key,
	String	value)
	{
	return super.put(key.toLowerCase(), value);
	}

public String getSource()
	{
	return source;
	}

public boolean isResponse()
	{
	return containsKey(RESPONSE);
	}

public boolean isEvent()
	{
	return containsKey(EVENT);
	}

public boolean equalsType(
	String	type)
	{
	return type.equalsIgnoreCase(getType());
	}

public String getType()
	{
	return isEvent() ? get(EVENT) : get(RESPONSE);
	}

public boolean isEqual(
	String	key,
	String	value)
	{
	return value.equals(get(key));
	}

public boolean isEqualIgnoreCase(
	String	key,
	String	value)
	{
	return value.equalsIgnoreCase(get(key));
	}

public String getChanVariable(
	int	chan,
	String	key)
	{
	String	channel = containsKey("Channel") ? get("Channel") : get("Channel" + chan);

	return get("ChanVariable(" + channel + "):" + key);
	}

public String toString()
	{
	return "Event : source " + source + " :: " + super.toString();
	}
}
