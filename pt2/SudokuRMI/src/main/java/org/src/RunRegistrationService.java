package org.src;

import org.src.model.registrationService.RegistrationService;
import org.src.model.registrationService.RegistrationServiceImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RunRegistrationService {
    public static final String REGISTRATION_SERVICE_NAME = "registrationService";
    public static void main(String[] args) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry();
        RegistrationService registrationService = new RegistrationServiceImpl();
        RegistrationService stub = (RegistrationService) UnicastRemoteObject.exportObject(registrationService, 0);
        registry.rebind(REGISTRATION_SERVICE_NAME, stub);
    }
}
