package com.gustavok.peach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class SenatorsManager {

    private static final List<Senator> senatorsList = new ArrayList<>();
    private static SenatorsManager INSTANCE = new SenatorsManager();

    static {
        Calendar c = Calendar.getInstance();
        c.set(25, 1, 1962);
        senatorsList.add(new Senator("Acir Gurgacz", c, "Cascavel (PR)", "PDT", "RO", "(61) 3303-3132 / 3131", "acir@senador.leg.br", "http://www.senado.leg.br/senadores/senador/acirgurgacz/default.htm", "senador4981.jpg"));
        c.set(10, 2, 1960);
        senatorsList.add(new Senator("Aécio Neves da Cunha", c, "Belo Horizonte (MG)", "PSDB", "MG", "(61) 3303-6049 / 6050", "aecio.neves@senador.leg.br", "", "senador391.jpg"));
        c.set(5, 3, 1945);
        senatorsList.add(new Senator("Aloysio Nunes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));       /*c.set(, 0, 19);         senatorsList.add(new Senator("", c, "", "", "", "", "", "", ""));         c.set(, 0, 19);         senatorsList.add(new Senator("", c, "", "", "", "", "", "", ""));         c.set(, 0, 19);         senatorsList.add(new Senator("", c, "", "", "", "", "", "", ""));         c.set(, 0, 19);         senatorsList.add(new Senator("", c, "", "", "", "", "", "", ""));*/
    }

    private SenatorsManager() {
    }

    public static SenatorsManager getInstance() {
        return INSTANCE;
    }

    public List<Senator> getSenatorsList() {
        return senatorsList;
    }
}
