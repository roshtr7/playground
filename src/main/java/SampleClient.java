import java.io.IOException;
import java.net.URISyntaxException;

public class SampleClient {

    public static void main(String[] theArgs) throws IOException, URISyntaxException {
        FhirService fhirService = new FhirService();
        fhirService.searchByFamilyName();
        fhirService.printPatientsName();
    }

}
