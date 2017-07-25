package ro.derbederos.crc;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class CRC32SlicingBy8Test extends AbstractCRCTest {

    public CRC32SlicingBy8Test(CRCModel crcModel) {
        super(crcModel, CRC32SlicingBy8Test::createCrc);
    }

    private static CRC createCrc(CRCModel crcModel) {
        return new CRC32SlicingBy8(
                (int) crcModel.getPoly(),
                (int) crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                (int) crcModel.getXorOut());
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() == 32)
                .collect(Collectors.toList());
    }
}
