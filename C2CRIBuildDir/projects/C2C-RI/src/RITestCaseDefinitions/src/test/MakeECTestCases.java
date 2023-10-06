/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author TransCore ITS, LLC
 */
public class MakeECTestCases {

    private HashMap<Integer, TestCaseSpec> testCases = new HashMap();
    private HashMap<String, String> subPubMap = new HashMap();

    public static void main(String[] args) {
        MakeECTestCases thisOne = new MakeECTestCases();
        System.out.println("Total number of Test Cases = " + thisOne.testCases.size());
        System.out.println("Test Case InventoryReport \n" + thisOne.getTestCaseInventoryReport());
    }

    public MakeECTestCases() {
        subPubMap.put("TCS-1-dlCenterActiveVerificationSubscription-EC-Valid.data", "dlCenterActiveVerificationUpdate");
        subPubMap.put("TCS-1-dlCenterActiveVerificationSubscription-OC-Valid.data", "dlCenterActiveVerificationUpdate");
        subPubMap.put("TCS-5-dlOrganizationInformationSubscription-EC-Valid.data", "dlOrganizationInformationUpdate");
        subPubMap.put("TCS-5-dlOrganizationInformationSubscription-OC-Valid.data", "dlOrganizationInformationUpdate");
        subPubMap.put("TCS-6-dlEventIndexSubscription-EC-Valid.data", "dlEventIndexUpdate");
        subPubMap.put("TCS-6-dlEventIndexSubscription-OC-Valid.data", "dlEventIndexUpdate");
        subPubMap.put("TCS-7-dlFullEventUpdateSubscription-OC-Valid.data", "dlFullEventUpdateUpdate");
        subPubMap.put("TCS-7-dlFullEventUpdateSubscription-EC-Valid.data", "dlFullEventUpdateUpdate");
        subPubMap.put("TCS-8-dlFullEventUpdateSubscription-OC-Valid.data", "dlFullEventUpdateUpdate");
        subPubMap.put("TCS-8-dlFullEventUpdateSubscription-EC-Valid.data", "dlFullEventUpdateUpdate");
        subPubMap.put("TCS-11-dlTrafficNetworkInformationSubscription-EC-Valid.data", "dlNodeInventoryUpdate");
        subPubMap.put("TCS-11-dlTrafficNetworkInformationSubscription-OC-Valid.data", "dlNodeInventoryUpdate");
        subPubMap.put("TCS-12-dlTrafficNetworkInformationSubscription-EC-Valid.data", "dlLinkInventoryUpdate");
        subPubMap.put("TCS-12-dlTrafficNetworkInformationSubscription-OC-Valid.data", "dlLinkInventoryUpdate");
        subPubMap.put("TCS-14-dlTrafficNetworkInformationSubscription-EC-Valid.data", "dlNodeStatusUpdate");
        subPubMap.put("TCS-14-dlTrafficNetworkInformationSubscription-OC-Valid.data", "dlNodeStatusUpdate");
        subPubMap.put("TCS-17-dlTrafficNetworkInformationSubscription-EC-Valid.data", "dlLinkStatusUpdate");
        subPubMap.put("TCS-17-dlTrafficNetworkInformationSubscription-OC-Valid.data", "dlLinkStatusUpdate");
        subPubMap.put("TCS-19-dlDeviceInformationSubscription-EC-Valid.data", "dlDetectorInventoryUpdate");
        subPubMap.put("TCS-19-dlDeviceInformationSubscription-OC-Valid.data", "dlDetectorInventoryUpdate");
        subPubMap.put("TCS-20-dlDeviceInformationSubscription-EC-Valid.data", "dlDetectorStatusUpdate");
        subPubMap.put("TCS-20-dlDeviceInformationSubscription-OC-Valid.data", "dlDetectorStatusUpdate");
        subPubMap.put("TCS-23-dlDetectorDataSubscription-EC-Valid.data", "dlDetectorDataUpdate");
        subPubMap.put("TCS-23-dlDetectorDataSubscription-OC-Valid.data", "dlDetectorDataUpdate");
        subPubMap.put("TCS-25-dlDeviceInformationSubscription-EC-Valid.data", "dlCCTVInventoryUpdate");
        subPubMap.put("TCS-25-dlDeviceInformationSubscription-OC-Valid.data", "dlCCTVInventoryUpdate");
        subPubMap.put("TCS-27-dlDeviceInformationSubscription-EC-Valid.data", "dlCCTVStatusUpdate");
        subPubMap.put("TCS-27-dlDeviceInformationSubscription-OC-Valid.data", "dlCCTVStatusUpdate");
        subPubMap.put("TCS-37-dlDeviceInformationSubscription-EC-Valid.data", "dlDMSInventoryUpdate");
        subPubMap.put("TCS-37-dlDeviceInformationSubscription-OC-Valid.data", "dlDMSInventoryUpdate");
        subPubMap.put("TCS-39-dlDeviceInformationSubscription-EC-Valid.data", "dlDMSStatusUpdate");
        subPubMap.put("TCS-39-dlDeviceInformationSubscription-OC-Valid.data", "dlDMSStatusUpdate");
        subPubMap.put("TCS-47-dlDeviceInformationSubscription-EC-Valid.data", "dlESSInventoryUpdate");
        subPubMap.put("TCS-47-dlDeviceInformationSubscription-OC-Valid.data", "dlESSInventoryUpdate");
        subPubMap.put("TCS-48-dlDeviceInformationSubscription-EC-Valid.data", "dlESSInventoryUpdate");
        subPubMap.put("TCS-48-dlDeviceInformationSubscription-OC-Valid.data", "dlESSInventoryUpdate");
        subPubMap.put("TCS-49-dlDeviceInformationSubscription-EC-Valid.data", "dlESSStatusUpdate");
        subPubMap.put("TCS-49-dlDeviceInformationSubscription-OC-Valid.data", "dlESSStatusUpdate");
        subPubMap.put("TCS-50-dlDeviceInformationSubscription-EC-Valid.data", "dlESSObservationReportUpdate");
        subPubMap.put("TCS-50-dlDeviceInformationSubscription-OC-Valid.data", "dlESSObservationReportUpdate");
        subPubMap.put("TCS-61-dlDeviceInformationSubscription-EC-Valid.data", "dlHARInventoryUpdate");
        subPubMap.put("TCS-61-dlDeviceInformationSubscription-OC-Valid.data", "dlHARInventoryUpdate");
        subPubMap.put("TCS-62-dlDeviceInformationSubscription-EC-Valid.data", "dlHARInventoryUpdate");
        subPubMap.put("TCS-62-dlDeviceInformationSubscription-OC-Valid.data", "dlHARInventoryUpdate");
        subPubMap.put("TCS-63-dlDeviceInformationSubscription-EC-Valid.data", "dlHARStatusUpdate");
        subPubMap.put("TCS-63-dlDeviceInformationSubscription-OC-Valid.data", "dlHARStatusUpdate");
        subPubMap.put("TCS-77-dlDeviceInformationSubscription-EC-Valid.data", "dlRampMeterInventoryUpdate");
        subPubMap.put("TCS-77-dlDeviceInformationSubscription-OC-Valid.data", "dlRampMeterInventoryUpdate");
        subPubMap.put("TCS-79-dlDeviceInformationSubscription-EC-Valid.data", "dlRampMeterStatusUpdate");
        subPubMap.put("TCS-79-dlDeviceInformationSubscription-OC-Valid.data", "dlRampMeterStatusUpdate");

        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url = "jdbc:odbc:Driver={Microsoft Access Driver "
                    + "(*.mdb, *.accdb)};DBQ=C:\\temp\\Final TMDD Requirements.accdb";
            try (Connection conn = DriverManager.getConnection(url))
			{
				System.out.println("Connected!");

				/**
				 * // SQL query command String SQL = "SELECT * FROM Customers"; stmt
				 * = con.createStatement(); rs = stmt.executeQuery(SQL); while
				 * (rs.next()) { System.out.println(rs.getString("Company") + " : "
				 * + rs.getString("First Name")+ " : " + rs.getString("Last Name"));
				 * }
				 *
				 */
				String SQL = "Select * from TestCaseDesignQuery";

				try (Statement stmt = conn.createStatement())
				{
					ResultSet rs = stmt.executeQuery(SQL);

					ArrayList<TestCaseSpec> testCaseList = new ArrayList();

					Integer ii = 0;
					while (rs.next()) {
						if (rs.getString("Target").equals("OC")) {
							TestCaseSpec thisTC = new TestCaseSpec();
							thisTC.setNeedId(rs.getString("UNID"));
							thisTC.setTestCaseName(rs.getString("TCID"));
							thisTC.setTestCaseDataFile(rs.getString("DataFile"));
							thisTC.setTestCaseDescription(rs.getString("Description"));
							thisTC.setTestCasePubDataFile(rs.getString("UpdateDataFile"));
							thisTC.setTestCasePubDescription(rs.getString("UpdateDescription"));
							completeTestCaseSpec(thisTC);
							testCases.put(ii, thisTC);
							ii++;
						}
					}
					rs.close();
				}
			}

		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		} catch (ClassNotFoundException cE) {
			System.out.println("Class Not Found Exception: "
					+ cE.toString());
		}


    }

    public Integer getNumberTestCases() {
        return testCases.size();
    }

    public String getTestCaseInventory() {
        StringBuffer results = new StringBuffer();

        for (Integer number : testCases.keySet()) {
            results = results.append(number).append("  -  ").append(testCases.get(number).getNeedId()).append("  -  ").append(testCases.get(number).getTestCaseName()).append("\n");
        }

        return results.toString();
    }

    public String getTestCaseInventoryReport() {
        StringBuffer results = new StringBuffer();

        for (Integer number : testCases.keySet()) {
            results = results.append(number).append("|").append(testCases.get(number).getNeedId()).append("|").append(testCases.get(number).getTestCaseName()).append("|").append(testCases.get(number).getTestCaseDescription()).append(testCases.get(number).getOperationName()).append("|").append(testCases.get(number).getRelatedOperationName()!=null?testCases.get(number).getRelatedOperationName():"").append("\n");
        }

        return results.toString();
    }
    
    public TestCaseSpec getTestCase(Integer id) {
        return testCases.get(id);
    }

    private void completeTestCaseSpec(TestCaseSpec thisSpec) {

        if (thisSpec.getTestCaseName().contains("Request")) {
            thisSpec.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR);
        } else {
            thisSpec.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB);
            thisSpec.setRelatedOperationName(subPubMap.get(thisSpec.getTestCasePubDataFile()));
            thisSpec.setNumPublicationsExpected(1);

        }
        thisSpec.setOperationName(getDialogFromTestCaseInfo(thisSpec.getTestCaseDataFile()));
        thisSpec.setSkipEncoding(false);
        thisSpec.setTransportErrorExpected(false);
        thisSpec.setEncodingErrorExpected(false);
        thisSpec.setMessageErrorExpected(false);
        thisSpec.setMessageValidationErrorExpected(true);
        thisSpec.setOperationErrorExptected(true);

        try {
            String subMessage = TestCaseSpec.SOAPENVELOPE.replace(TestCaseSpec.BODYPLACEHOLDERTEXT, new String(readFile("C:\\temp\\data\\301XMLSamples\\" + thisSpec.getTestCaseDataFile() + ".xml"), "UTF-8").replace("208.206.232.40", "localhost"));
            thisSpec.setRequestMessage(subMessage.getBytes("UTF-8"));
            thisSpec.setRelatedResponseMessage(("<c2c:c2cMessageReceipt xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\"><informationalText>Response for test case " + thisSpec.getTestCaseName() + "</informationalText></c2c:c2cMessageReceipt>").getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
            thisSpec.setRequestMessage(ex.getMessage().getBytes());
            thisSpec.setRelatedResponseMessage(ex.getMessage().getBytes());
        }

    }

    private static String getDialogFromTestCaseInfo(String testCaseInfo) {
        String result = "";
        if ((testCaseInfo != null) && (!testCaseInfo.isEmpty())) {
            if (testCaseInfo.contains("Request")) {
                result = testCaseInfo.substring(testCaseInfo.indexOf("-dl") + 1, testCaseInfo.indexOf("Request") + 7);
            } else if (testCaseInfo.contains("Subscription")) {
                result = testCaseInfo.substring(testCaseInfo.indexOf("-dl") + 1, testCaseInfo.indexOf("Subscription") + 12);
            } else if (testCaseInfo.contains("Update")) {
                result = testCaseInfo.substring(testCaseInfo.indexOf("-dl") + 1, testCaseInfo.indexOf("Update") + 6);
            }
        }
        return result.trim();

    }

    private static byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    private static byte[] readFile(File file) throws IOException {
        // Open file
        try (RandomAccessFile f = new RandomAccessFile(file, "r"))
		{
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) {
                throw new IOException("File size >= 2 GB");
            }

            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        }
    }
}
