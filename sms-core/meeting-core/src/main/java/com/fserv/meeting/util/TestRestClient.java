package com.fserv.meeting.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fserv.common.util.JsonUtils;

public class TestRestClient {

	private static String httpUrl = "https://www.followmyhealth.com/api/patientaccess/autocomplete";

	public static void main(String[] args) throws UnsupportedEncodingException {
		RestTemplate restClient = new RestTemplate();

//    loadHealthConditionsAndWriteToFile(restClient);
//    loadProceduresAndWriteToFile(restClient);
//    loadFamilyHealthConditionsAndWriteToFile(restClient);
//    loadPersonalHealthConditionsAndWriteToFile(restClient);

		// loadAllergiesAndWriteToFile(restClient);
		// loadAllergicReactionsAndWriteToFile(restClient);
//    loadImmunizationsAndWriteToFile(restClient);
		loadLabTestResultTypesAndWriteToFile(restClient);
	}

	private static void loadHealthConditionsAndWriteToFile(RestTemplate restClient) {
		String dictionaryName = "Health Conditions";
		Map<String, JsonNode> allAllergiesMap = loadDictionary(restClient, dictionaryName);
		writeDictionaryDataToFile(allAllergiesMap, "C:\\tmp\\healthconditions.json");
	}

	private static void loadProceduresAndWriteToFile(RestTemplate restClient) {
		String dictionaryName = "Procedures";
		Map<String, JsonNode> allAllergiesMap = loadDictionary(restClient, dictionaryName);
		writeDictionaryDataToFile(allAllergiesMap, "C:\\tmp\\procedures.json");
	}

	private static void loadFamilyHealthConditionsAndWriteToFile(RestTemplate restClient) {
		String dictionaryName = "Family Health Considerations";
		Map<String, JsonNode> allAllergiesMap = loadDictionary(restClient, dictionaryName);
		writeDictionaryDataToFile(allAllergiesMap, "C:\\tmp\\familyhealthconditions.json");
	}

	private static void loadPersonalHealthConditionsAndWriteToFile(RestTemplate restClient) {
		String dictionaryName = "Personal Health Considerations";
		Map<String, JsonNode> allAllergiesMap = loadDictionary(restClient, dictionaryName);
		writeDictionaryDataToFile(allAllergiesMap, "C:\\tmp\\personalhealthconditions.json");
	}

	// https://www.followmyhealth.com/api/patientaccess/autocomplete?dictionary=Results&term=a&maxResults=15&_=1582421737393
	private static void loadLabTestResultTypesAndWriteToFile(RestTemplate restClient) {
		String dictionaryName = "Results";
		Map<String, JsonNode> allAllergiesMap = loadDictionary(restClient, dictionaryName);
		writeDictionaryDataToFile(allAllergiesMap, "C:\\tmp\\testresulttypes.json");
	}

	private static void loadImmunizationsAndWriteToFile(RestTemplate restClient) {
		String dictionaryName = "Immunizations";
		Map<String, JsonNode> allAllergiesMap = loadDictionary(restClient, dictionaryName);
		writeDictionaryDataToFile(allAllergiesMap, "C:\\tmp\\immunizations.json");
	}

	private static void loadAllergiesAndWriteToFile(RestTemplate restClient) {
		String dictionaryName = "Non-Medication Allergies";
		Map<String, JsonNode> allAllergiesMap = loadDictionary(restClient, dictionaryName);
		writeDictionaryDataToFile(allAllergiesMap, "C:\\tmp\\allergies.json");
	}

	private static void loadAllergicReactionsAndWriteToFile(RestTemplate restClient) {
		String dictionaryName = "Allergic Reactions";
		Map<String, JsonNode> allAllergiesMap = loadDictionary(restClient, dictionaryName);
		writeDictionaryDataToFile(allAllergiesMap, "C:\\tmp\\allergic-reactions.json");
	}

	private static void writeDictionaryDataToFile(Map<String, JsonNode> allAllergiesMap, String filePath) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(filePath)));
			bw.write(JsonUtils.writeValueAsString(allAllergiesMap.values()));
			bw.flush();

			// for (Map.Entry<String, String> allAllergyEntry : allAllergiesMap.entrySet())
			// {
			// bw.write(Jsonutil.toJsonObj(allAllergyEntry.getValue()));
			// bw.flush();
			// }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static Map<String, JsonNode> loadDictionary(RestTemplate restClient, String dictionaryName) {
		Map<String, JsonNode> dictionaryMap = new TreeMap<>();
		int ci = 'a';
		for (; ci <= 'z'; ci++) {
			char c = (char) ci;
			addAllergyToMap(restClient, dictionaryMap, dictionaryName, String.valueOf(c));
		}
		return dictionaryMap;
	}

	private static void addAllergyToMap(RestTemplate restClient, Map<String, JsonNode> allAllergiesMap,
			String allergyDictionary, String term) {
		JsonNode aJsonNode = getAllergies(allergyDictionary, term, restClient);

		if (aJsonNode.isArray()) {
			aJsonNode.forEach(jNode -> {
				String jNodeVal = jNode.get("Value").asText();
				allAllergiesMap.put(jNodeVal, jNode);
			});
		}
	}

	private static JsonNode getAllergies(String dictionary, String term, RestTemplate restClient) {
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.put("dictionary", Arrays.asList(dictionary));
		queryParams.put("term", Arrays.asList(term));
		queryParams.put("UseSimilar", Arrays.asList(Boolean.TRUE.toString()));
		queryParams.put("maxResults", Arrays.asList("10000"));
		queryParams.put("_", Arrays.asList("1582327123755"));

		ResponseEntity<JsonNode> response = restClient.getForEntity(
				UriComponentsBuilder.fromHttpUrl(httpUrl).queryParams(queryParams).build(false).toUriString(),
				JsonNode.class);
		return response.getBody();
	}

}
