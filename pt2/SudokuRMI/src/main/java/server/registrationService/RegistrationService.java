package server.registrationService;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationService extends Remote {

    void registerSudoku(String sudokuId) throws RemoteException;

}
