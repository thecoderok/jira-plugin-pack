/**
 * ABJiraSoapService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package bugtracker.rpc.soap.abws_admin;

public interface ABJiraSoapService extends java.rmi.Remote {
    public java.lang.String login(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException, com.atlassian.jira.rpc.exception.RemoteAuthenticationException, com.atlassian.jira.rpc.exception.RemoteException;
    public boolean logout(java.lang.String in0) throws java.rmi.RemoteException;
    public java.lang.String startReindex(java.lang.String in0) throws java.rmi.RemoteException, com.atlassian.jira.rpc.exception.RemoteException;
    public java.lang.String[] checkIntegrity(java.lang.String in0) throws java.rmi.RemoteException, com.atlassian.jira.rpc.exception.RemoteException;
}
