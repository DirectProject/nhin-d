package org.nhindirect.policy;

import java.util.Vector;

public interface ExecutionEngine 
{
	public Boolean evaluate(Vector<Opcode> opcodes) throws PolicyProcessException;
}
