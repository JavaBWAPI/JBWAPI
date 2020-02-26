package bwapi;

import org.junit.Test;

import java.io.IOException;

public class DrawTest {


    @Test
    public void drawingHugeStringShouldTruncate() throws IOException {
        final String test = "L: PvP Jadien @ (4)Gladiator1.1.scx (PvPRobo, Fingerprint2Gate, FingerprintDragoonRange, FingerprintGatewayFirst, FingerprintMannerPylon, FingerprintProxyGateway)\n" +
                "W: PvP Jadien @ (3)Power Bond.scx (PvP2GateDTExpand)\n" +
                "W: PvP Jadien @ (2)Overwatch(n).scx (PvP2GateDTExpand, Fingerprint1GateCore, Fingerprint4GateGoon, FingerprintDragoonRange, FingerprintGatewayFirst, FingerprintMannerPylon)\n" +
                "L: PvP Jadien @ (4)Fighting Spirit.scx (PvPProxy2Gate, Fingerprint1GateCore, Fingerprint2Gate, FingerprintDragoonRange, FingerprintGatewayFirst, FingerprintMannerPylon)\n" +
                "L: TvT Atlas Wing @ (2)Overwatch(n).scx (TvT1FacFE, TvT2BaseBC, FingerprintBio)\n" +
                "W: TvT Epsilon Squadron @ (4)Gladiator1.1.scx (TvT2FacTanks, TvT5Fac, FingerprintBio)\n" +
                "W: TvT Epsilon Squadron @ (4)Fighting Spirit.scx (TvT2FacTanks, TvT5Fac, Fingerprint2Rax1113, FingerprintBio)\n" +
                "W: TvT Mar Sara @ (2)Tres Pass.scx (TvT2FacTanks, TvT5Fac, FingerprintBio)\n" +
                "W: TvT Epsilon Squadron @ (4)Fighting Spirit.scx (TvT2FacTanks, TvT5Fac, FingerprintBio)\n" +
                "W: TvT Antiga @ (4)CircuitBreakers1.0.scx (TvT1RaxFE, TvT5Fac, FingerprintBio)\n" +
                "W: TvT Epsilon Squadron @ (4)CircuitBreakers1.0.scx (TvT2FacTanks, TvT5Fac, FingerprintBio)\n" +
                "W: TvT Elite Guard @ (2)Overwatch(n).scx (TvT1FacFE, TvT2BaseBC, FingerprintBBS, FingerprintBio)\n" +
                "W: TvT Delta Squadron @ (4)Fighting Spirit.scx (TvT1RaxFE, TvT2Base2Port, TvT2BaseBC, FingerprintBBS, FingerprintBio)\n" +
                "W: TvT Mar Sara @ (2)Tres Pass.scx (TvT1FacPort, TvT5Fac, FingerprintBBS, FingerprintBio)\n" +
                "W: TvT Delta Squadron @ (3)Power Bond.scx (TvT1FacFE, TvT2Base2Port, TvT2BaseBC, FingerprintBio)\n" +
                "L: TvT Delta Squadron @ (2)Overwatch(n).scx (TvE1RaxSCVMarine, FingerprintBio)\n" +
                "W: TvT Epsilon Squadron @ (4)Fighting Spirit.scx (TvT2FacTanks, TvT5Fac, FingerprintBio)\n" +
                "W: TvT Cronus Wing @ (2)Overwatch(n).scx (TvT2Port, TvT2Base2Port, TvT2BaseBC, FingerprintBio)\n" +
                "W: TvT Epsilon Squadron @ (4)CircuitBreakers1.0.scx (TvT2FacTanks, TvT5Fac, FingerprintBio)\n" +
                "W: TvT Antiga @ (3)Power Bond.scx (TvT1RaxFE, TvT5Fac, FingerprintBio)\n" +
                "W: TvT Cronus Wing @ (4)Gladiator1.1.scx (TvT2FacTanks, TvT2Base2Port, TvT2BaseBC, FingerprintBBS, FingerprintBio)\n" +
                "L: TvT Kel-Morian Combine @ (4)Gladiator1.1.scx (TvT14CC, TvT5Fac)\n" +
                "W: TvT Cronus Wing @ (4)Fighting Spirit.scx (TvT1FacPort, TvT2Base2Port, TvT2BaseBC, FingerprintBBS, FingerprintBio)\n" +
                "W: TvT Cronus Wing @ (2)Overwatch(n).scx (TvT2FacTanks, TvT2Base2Port, TvT2BaseBC, FingerprintBio)\n" +
                "W: TvT Kel-Morian Combine @ (4)CircuitBreakers1.0.scx (TvE2RaxSCVMarine, FingerprintBio)\n" +
                "W: TvT Atlas Wing @ (4)Fighting Spirit.scx (TvT1FacFE, TvT2BaseBC, FingerprintBBS, FingerprintBio)\n" +
                "W: TvT Epsilon Squadron @ (2)Tres Pass.scx (TvT2FacTanks, TvT5Fac, FingerprintBio, FingerprintWorkerRush)\n" +
                "W: TvT Antiga @ (4)Gladiator1.1.scx (TvT1RaxFE, TvT5Fac, Fingerprint2Rax1113, FingerprintBio)\n" +
                "L: TvT Delta Squadron @ (4)CircuitBreakers1.0.scx (TvEProxyBBS)\n" +
                "L: TvT Cronus Wing @ (4)Fighting Spirit.scx (TvEProxyBBS, FingerprintBio)";

        final Game game = GameBuilder.createGame();

        game.drawTextScreen(0, 0, test);
    }
}
