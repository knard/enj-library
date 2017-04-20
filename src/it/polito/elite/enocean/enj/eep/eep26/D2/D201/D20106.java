package it.polito.elite.enocean.enj.eep.eep26.D2.D201;

import it.polito.elite.enocean.enj.communication.EnJConnection;
import it.polito.elite.enocean.enj.eep.EEPIdentifier;
import it.polito.elite.enocean.enj.eep.eep26.attributes.*;

public class D20106 extends D201 {

    // the type definition
    public static final byte type = (byte) 0x06;

    // the ON state / command
    public static boolean ON = true;

    // the ON command byte
    public static byte ON_BYTE = (byte) 0x01;

    // the OFF state / command
    public static boolean OFF = false;

    // the OFF command byte
    public static byte OFF_BYTE = (byte) 0x00;

    // the byte identifier for all output channels
    public static byte ALL_OUTPUT_CHANNEL = 0x00;

    /**
     * Builds a new EEPProfile instance of type D2.01.06 as specified in the
     * EEP2.6 specification.
     */
    public D20106() {
        super();

        // add the supported functions
        this.addChannelAttribute(0, new EEP26Switching());
        this.addChannelAttribute(31, new EEP26EnergyMeasurement());
    }

    public void actuatorSetOuput(EnJConnection connection, byte[] deviceAddress, boolean command)
    {
        // exec the command by using the D201 general purpose implementation
        super.actuatorSetOutput(connection,
                deviceAddress,
                D201DimMode.SWITCH_TO_NEW_OUTPUT_VALUE.getCode(),
                D20106.ALL_OUTPUT_CHANNEL,
                command ? D20106.ON_BYTE : D20106.OFF_BYTE);
    }

    public void actuatorSetMeasurement(EnJConnection connection,
                                       byte[] deviceAddress, boolean autoReportMesurement,
                                       boolean signalResetMeasurement, boolean powerMode, int channelId,
                                       int measurementDeltaToBeReported, D201UnitOfMeasure unitOfMeasure,
                                       int maximumTimeBetweenActuatorMessages,
                                       int minimumTimeBetweenActuatorMessages)
    {
        if ((maximumTimeBetweenActuatorMessages >= 0)
                && (minimumTimeBetweenActuatorMessages >= 0))
        {
            byte reportMeasurementAsByte = autoReportMesurement ? (byte) 0x01 : (byte) 0x00;
            byte signalResetMeasurementAsByte = signalResetMeasurement ? (byte) 0x01 : (byte) 0x00;
            byte powerModeAsByte = powerMode ? (byte) 0x01 : (byte) 0x00;

            // get the measurement delta LSB, and with all 0 apart from the last
            // 4 bits
            byte measurementDeltaLSB = (byte) ((measurementDeltaToBeReported) & (0x000F));

            // get the measurement delta MSB, shift right by 8 bits and set at 0
            // the 8 leading bits
            byte measurementDeltaMSB = (byte) ((measurementDeltaToBeReported >> 8) & (0x00FF));
            byte maximumTimeAsByte = (byte) Math.min((maximumTimeBetweenActuatorMessages / 10), 255);
            byte minimumTimeAsByte = (byte) Math.min(minimumTimeBetweenActuatorMessages, 255);

            // call the superclass method
            super.actuatorSetMeasurement(connection, deviceAddress,
                    reportMeasurementAsByte, signalResetMeasurementAsByte,
                    powerModeAsByte, (byte) channelId, measurementDeltaLSB,
                    unitOfMeasure.getCode(), measurementDeltaMSB,
                    maximumTimeAsByte, minimumTimeAsByte);
        }
        else {
            throw new NumberFormatException("Only positive numbers allowed for time values");
        }
    }

    /**
     * Asks for the current power or energy measurement on a given channel Id of
     * a given EnOcean actuator
     *
     * @param connection
     * @param deviceAddress
     * @param channelId
     */
    public void actuatorMeasurementQuery(EnJConnection connection, byte[] deviceAddress, int channelId)
    {
        // call the superclass method
        super.actuatorMeasurementQuery(connection, deviceAddress, (byte) 0x00, (byte) channelId);
    }

    @Override
    public EEPIdentifier getEEPIdentifier() {
        return new EEPIdentifier(D201.rorg, D201.func, D20106.type);
    }
}
