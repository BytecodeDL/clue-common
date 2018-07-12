package org.clyze.persistent.model.doop;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import org.clyze.persistent.model.Position;
import org.clyze.persistent.model.Symbol;

public class MethodInvocation extends Symbol {

	private String name;

	private String invokingMethodDoopId;

	private String doopId;

	/** is inside instance initializer block */
	private boolean inIIB = false;

    public MethodInvocation() {}

	public MethodInvocation(Position position, 
                            String sourceFileName, 
                            String name, 
                            String doopId, 
                            String invokingMethodDoopId,
                            boolean inIIB) {
		super(position, sourceFileName);
		this.name = name;
		this.doopId = doopId;
		this.invokingMethodDoopId = invokingMethodDoopId;
		this.inIIB = inIIB;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInvokingMethodDoopId() {
        return invokingMethodDoopId;
    }

    public void setInvokingMethodDoopId(String invokingMethodDoopId) {
        this.invokingMethodDoopId = invokingMethodDoopId;
    }

    public String getDoopId() {
        return doopId;
    }

    public void setDoopId(String doopId) {
        this.doopId = doopId;
    }

    public boolean getInIIB() {
        return inIIB;
    }

    public void setInIIB(boolean inIIB) {
        this.inIIB = inIIB;
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        MethodInvocation that = (MethodInvocation) object;        
        return inIIB == that.inIIB &&
                Objects.equals(name, that.name) &&
                Objects.equals(invokingMethodDoopId, that.invokingMethodDoopId) &&
                Objects.equals(doopId, that.doopId);                    
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), name, invokingMethodDoopId, doopId, inIIB);       
    }

    protected void saveTo(Map<String, Object> map) {
		super.saveTo(map);
		map.put("name", this.name);
		map.put("invokingMethodDoopId", this.invokingMethodDoopId);
        map.put("doopId", this.doopId);
		map.put("inIIB", this.inIIB);
	}

	protected void loadFrom(Map<String, Object> map){
		super.loadFrom(map);
		this.name                 = (String) map.get("name");
		this.invokingMethodDoopId = (String) map.get("invokingMethodDoopId");
        this.doopId               = (String) map.get("doopId");
		this.inIIB                = (Boolean) map.get("inIIB");
	}
}
