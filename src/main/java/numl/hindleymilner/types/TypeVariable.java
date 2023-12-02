package numl.hindleymilner.types;

import lombok.Getter;
import lombok.Setter;
import numl.hindleymilner.IType;

import java.nio.charset.StandardCharsets;

/**
 A type variable standing for an arbitrary type.
 <p>
 All type variables have a unique id, but names are only assigned lazily,
 when required.
 */
@Getter
public class TypeVariable implements IType
{
	static int nextVariableId=0;
	static String nextVariableName="a";
	private int id;
	@Setter
	private IType instance;
	private String name;

	public TypeVariable()
	{
		this.id=TypeVariable.nextVariableId+=1;
		this.instance=null;
		this.name=null;
	}

	public static TypeVariable TypeVariable()
	{
		return new TypeVariable();
	}

	/**
	 Names are allocated to TypeVariables lazily, so that only TypeVariables
	 present after analysis consume names.
	 */
	public String getName()
	{
		if(this.name==null)
			this.name=TypeVariable.nextVariableName=String.valueOf((char)(ord(TypeVariable.nextVariableName)+1));
		return this.name;
	}

	public static int ord(String s)
	{
		return s.length()>0?(s.getBytes(StandardCharsets.UTF_8)[0]&0xff):0;
	}

	public static int ord(char c)
	{
		return c<0x80?c:ord(Character.toString(c));
	}

	@Override
	public String toString()
	{
		return this.instance!=null?this.instance.toString():this.name;
	}
}

