package common.user;

import java.awt.*;
import java.io.Serializable;

public interface UserData extends Serializable {

    String name();

    Color color();

}