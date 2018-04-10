package electronicLab.model;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.UnsupportedCommOperationException;

public class SerialTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testSerial() throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException {
		Serial serial = new Serial("COM1");
	}

}
