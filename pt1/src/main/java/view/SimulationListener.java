package view;

import logic.Road;
import logic.TrafficLight;
import java.util.List;
import logic.Car;


public interface SimulationListener {

    void notifyInit(int t, List<Car> agents);

    void notifyStepDone(int t, List<Road> roads, List<Car> agents, List<TrafficLight> trafficLights);

    void notifySimulationEnded();

    void notifyStat(double averageSpeed);

}
