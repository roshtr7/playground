import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PrimitiveType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FhirService {

    private List<String> nameList;
    private FhirContext fhirContext;
    private IGenericClient client;

    private HttpClientInterceptor httpClientInterceptor;

    private void init() throws IOException, URISyntaxException {
        // Create a FHIR client
        readData();
        this.fhirContext = FhirContext.forR4();
        this.client = fhirContext.newRestfulGenericClient(FhirConstants.FHIR_CLIENT_URL);
        httpClientInterceptor = new HttpClientInterceptor();
//      this.client.registerInterceptor(new LoggingInterceptor(false));
        this.client.registerInterceptor(httpClientInterceptor);
    }

    public void printPatientsName() throws IOException, URISyntaxException {
        init();
        // Search for Patient resources
        Bundle response = this.client
                .search()
                .forResource("Patient")
                .sort(new SortSpec("given"))
                .count(20)
                .returnBundle(Bundle.class)
                .execute();

        List<Bundle.BundleEntryComponent> entry = response.getEntry();
        entry.sort(FhirService::compareByName);
        entry.forEach(bundleEntryComponent -> {
            Patient pt23 = (Patient) bundleEntryComponent.getResource();
            pt23.getName().forEach(humanName -> {
                System.out.println(FhirConstants.FIRST_NAME + ":" + humanName.getGiven().stream().findFirst().map(PrimitiveType::getValue).orElse(""));
                System.out.println(FhirConstants.LAST_NAME + ":" + Optional.ofNullable(humanName.getFamily()).orElse(""));
                System.out.println(FhirConstants.DOB + ":" + pt23.getBirthDate());
            });
        });
    }

    public static int compareByName(Bundle.BundleEntryComponent lhs, Bundle.BundleEntryComponent rhs) {
        Patient pt23 = (Patient) lhs.getResource();
        String s1 = pt23.getName().stream().findFirst().map(humanName -> humanName.getGiven().stream().findFirst().map(PrimitiveType::getValue).map(String::toLowerCase).orElse("")).orElse("");
        Patient pt24 = (Patient) rhs.getResource();
        String s2 = pt24.getName().stream().findFirst().map(humanName -> humanName.getGiven().stream().findFirst().map(PrimitiveType::getValue).map(String::toLowerCase).orElse("")).orElse("");
        return s1.compareTo(s2);
    }

    public void searchByFamilyName() throws IOException, URISyntaxException {
        init();
        int i = 0;
        List<Long> timeList = new ArrayList<>();
        while (i < 3) {
            long start = System.currentTimeMillis();
            if (i < 2) {
                nameList.forEach(s -> {
                    Bundle results = client
                            .search()
                            .forResource(Patient.class)
                            .where(Patient.FAMILY.matches().value(s))
                            .returnBundle(Bundle.class)
                            .execute();
                });
            } else {
                nameList.forEach(s -> {
                    Bundle results = client
                            .search()
                            .forResource(Patient.class)
                            .where(Patient.FAMILY.matches().value(s))
                            .returnBundle(Bundle.class)
                            .cacheControl(new CacheControlDirective().setNoCache(true))
                            .execute();
                });
            }
            i++;
            long elapsedTime = System.currentTimeMillis() - start;
            timeList.add(elapsedTime);
        }
        System.out.println("Average time taken : ");
        timeList.forEach(System.out::println);

    }

    public void readData() throws IOException, URISyntaxException {
        this.nameList = Files.readAllLines(Paths.get(FhirService.class.getResource("patients.txt").toURI()), StandardCharsets.UTF_8);
    }
}
