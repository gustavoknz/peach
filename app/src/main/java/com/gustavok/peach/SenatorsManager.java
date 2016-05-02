package com.gustavok.peach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class SenatorsManager {
    static {
        /*
        Calendar c = Calendar.getInstance();
        c.set(25, 1, 1962);
        senatorsList.add(new Senator("Acir Gurgacz", c, "Cascavel (PR)", "PDT", "RO", "(61) 3303-3132 / 3131", "acir@senador.leg.br", "http://www.senado.leg.br/senadores/senador/acirgurgacz/default.htm", "senador4981.jpg"));
        c.set(10, 2, 1960);
        senatorsList.add(new Senator("Aécio Neves da Cunha", c, "Belo Horizonte (MG)", "PSDB", "MG", "(61) 3303-6049 / 6050", "aecio.neves@senador.leg.br", "", "senador391.jpg"));
        c.set(5, 3, 1945);
        senatorsList.add(new Senator("Aloysio Nunes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("3Aloysio Nunes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("A34loysio Nunes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Alo453ysio Nunes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysi45o Nunes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio 45Nunes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nu45nes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nu435nes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nun45es Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nune45s Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nunes45 Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nunes 45Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nunes F453erreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nunes Fer435reira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nunes Ferre435ira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nunes Ferreira", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        senatorsList.add(new Senator("Aloysio Nunes Ferrei43ra", c, "São José do Rio Preto (SP)", "PSDB", "SP", "(61) 3303-6063 / 6064", "aloysionunes.ferreira@senador.leg.br", "", "senador846.jpg"));
        */
    }

    private List<Senator> getVotes() {
        return RestClient.getAllVotes();
    }
}
