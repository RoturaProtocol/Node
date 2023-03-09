package brs.block;

import brs.Block;
import brs.Blockchain;
import brs.Burst;
import brs.assetexchange.AssetExchange;
import brs.common.QuickMocker;
import brs.fluxcapacitor.FluxCapacitor;
import brs.fluxcapacitor.FluxCapacitorImpl;
import brs.fluxcapacitor.FluxValues;
import brs.props.PropertyService;
import brs.services.ParameterService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Burst.class)
public class BlockImplTest {

  private Blockchain blockchainMock;
  private PropertyService propertyServiceMock;

  private FluxCapacitorImpl t;

  @Before
  public void setUp() {
    blockchainMock = Mockito.mock(Blockchain.class);
  }


  @PrepareForTest(Burst.class)
  @DisplayName("Feature is not active on ProdNet")
  @Test
  public void getAverageCommitment() {
   // when(blockchainMock.getHeight()).thenReturn(499999);

    mockStatic(Burst.class);
    final FluxCapacitor fluxCapacitor = QuickMocker.fluxCapacitorEnabledFunctionalities(FluxValues.DIGITAL_GOODS_STORE);
    Mockito.when(Burst.getFluxCapacitor()).thenReturn(fluxCapacitor);

    String payLoadHashStr = "payLoadHash";
    String generatorPublicKey = "generatorPublicKey";
    String generationSignature = "generationSignature";
    String blockSignature = "blockSignature";
    String previousBlockHash = "previousBlockHash";
    String ats = "ats";

    try {
      Block block = new Block(0,0,0,0,0,0,
        payLoadHashStr.getBytes(StandardCharsets.UTF_8),
        generatorPublicKey.getBytes(StandardCharsets.UTF_8),
        generationSignature.getBytes(StandardCharsets.UTF_8),
        blockSignature.getBytes(StandardCharsets.UTF_8),
        previousBlockHash.getBytes(StandardCharsets.UTF_8),
        new BigInteger("1"),
        0,
        0,
        0,
        0l,
        0,ats.getBytes(StandardCharsets.UTF_8));
      //doReturn(block).when(blockchain).getLastBlock();
      long averageCommitent = block.getAverageCommitment();
      System.out.println("averageCommitent...");
      System.out.println(averageCommitent);

      assertFalse(t.getValue(FluxValues.POC2));
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
