/**
 * ABJiraSoapServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package bugtracker.rpc.soap.abws_admin;

public interface ABJiraSoapServiceService extends javax.xml.rpc.Service {
    public java.lang.String getAbwsAdminAddress();

    public bugtracker.rpc.soap.abws_admin.ABJiraSoapService getAbwsAdmin() throws javax.xml.rpc.ServiceException;

    public bugtracker.rpc.soap.abws_admin.ABJiraSoapService getAbwsAdmin(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
