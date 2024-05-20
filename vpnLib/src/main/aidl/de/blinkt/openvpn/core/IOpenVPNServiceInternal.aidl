package de.blinkt.openvpn.core;

/**
 * Created by arne on 15.11.16.
 */

interface IOpenVPNServiceInternal {

    boolean protect(int fd);

    void userPause(boolean b);

    /**
     * @param replaceConnection True if the VPN is connected by a new connection.
     * @return true if there was a process that has been send a stop signal
     */
    boolean stopVPN(boolean replaceConnection);

    void addAllowedExternalApp(String packagename);

    boolean isAllowedExternalApp(String packagename);

    void challengeResponse(String repsonse);
}
